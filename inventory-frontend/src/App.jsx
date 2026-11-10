
import { useEffect, useMemo, useState } from 'react'
import { productService } from './services/productService'
import './App.css'

const emptyForm = {
  name: '',
  category: '',
  price: '',
  quantity: '',
}

function App() {
  const [products, setProducts] = useState([])
  const [summary, setSummary] = useState({
    totalProducts: 0,
    totalStockUnits: 0,
    totalInventoryValue: 0,
    lowStockProductCount: 0,
  })

  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [search, setSearch] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  async function loadDashboard() {
    setLoading(true)
    setError('')

    try {
      const [productData, summaryData] = await Promise.all([
        productService.getAll(),
        productService.getSummary(),
      ])

      setProducts(productData)
      setSummary(summaryData)
    } catch (err) {
      setError(err.message || 'Unable to load inventory data.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadDashboard()
  }, [])

  const filteredProducts = useMemo(() => {
    return products.filter((product) =>
        `${product.name} ${product.category}`
            .toLowerCase()
            .includes(search.toLowerCase())
    )
  }, [products, search])

  function handleInputChange(event) {
    const { name, value } = event.target

    setForm((current) => ({
      ...current,
      [name]: value,
    }))
  }

  function openAddForm() {
    setForm(emptyForm)
    setEditingId(null)
    setShowForm(true)
    setError('')
    setSuccess('')
  }

  function openEditForm(product) {
    setForm({
      name: product.name,
      category: product.category,
      price: String(product.price),
      quantity: String(product.quantity),
    })

    setEditingId(product.id)
    setShowForm(true)
    setError('')
    setSuccess('')
  }

  function closeForm() {
    setShowForm(false)
    setEditingId(null)
    setForm(emptyForm)
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setSuccess('')

    const product = {
      name: form.name.trim(),
      category: form.category.trim(),
      price: Number(form.price),
      quantity: Number(form.quantity),
    }

    try {
      if (editingId !== null) {
        await productService.update(editingId, product)
        setSuccess('Product updated successfully.')
      } else {
        await productService.create(product)
        setSuccess('Product added successfully.')
      }

      closeForm()
      await loadDashboard()
    } catch (err) {
      setError(err.message || 'Unable to save product.')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id) {
    const confirmed = window.confirm(
        'Are you sure you want to delete this product?'
    )

    if (!confirmed) return

    setError('')
    setSuccess('')

    try {
      await productService.delete(id)
      setSuccess('Product deleted successfully.')
      await loadDashboard()
    } catch (err) {
      setError(err.message || 'Unable to delete product.')
    }
  }

  const currency = (value) =>
      Number(value || 0).toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      })

  return (
      <div className="app">
        <aside className="sidebar">
          <div className="brand">
            <div className="brand-icon">S</div>
            <div>
              <h2>SmartStock</h2>
              <span>Inventory Manager</span>
            </div>
          </div>

          <nav>
            <a className="nav-link active" href="#dashboard">
              <span>▦</span> Dashboard
            </a>
            <a className="nav-link" href="#products">
              <span>▤</span> Products
            </a>
          </nav>

          <div className="sidebar-footer">
            Inventory Management System
            <span>React + Spring Boot</span>
          </div>
        </aside>

        <main className="main-content">
          <header className="topbar">
            <div>
              <h1>Inventory Dashboard</h1>
              <p>Monitor and manage your inventory</p>
            </div>

            <button className="primary-button" onClick={openAddForm}>
              + Add Product
            </button>
          </header>

          {error && <div className="alert error">{error}</div>}
          {success && <div className="alert success">{success}</div>}

          <section className="stats-grid" id="dashboard">
            <StatCard
                label="Total Products"
                value={summary.totalProducts}
                icon="▤"
            />
            <StatCard
                label="Total Stock Units"
                value={summary.totalStockUnits}
                icon="▦"
            />
            <StatCard
                label="Inventory Value"
                value={currency(summary.totalInventoryValue)}
                icon="€"
            />
            <StatCard
                label="Low Stock Products"
                value={summary.lowStockProductCount}
                icon="!"
                warning
            />
          </section>

          <section className="panel" id="products">
            <div className="panel-header">
              <div>
                <h2>Products</h2>
                <p>Manage your inventory records</p>
              </div>

              <input
                  className="search-input"
                  type="search"
                  placeholder="Search name or category..."
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
              />
            </div>

            {loading ? (
                <div className="empty-state">Loading inventory...</div>
            ) : filteredProducts.length === 0 ? (
                <div className="empty-state">No products found.</div>
            ) : (
                <div className="table-wrapper">
                  <table>
                    <thead>
                    <tr>
                      <th>ID</th>
                      <th>Product</th>
                      <th>Category</th>
                      <th>Price</th>
                      <th>Quantity</th>
                      <th>Stock Status</th>
                      <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {filteredProducts.map((product) => (
                        <tr key={product.id}>
                          <td>#{product.id}</td>
                          <td className="product-name">{product.name}</td>
                          <td>{product.category}</td>
                          <td>{currency(product.price)}</td>
                          <td>{product.quantity}</td>
                          <td>
                        <span
                            className={
                              product.quantity < 5
                                  ? 'stock-badge low'
                                  : 'stock-badge normal'
                            }
                        >
                          {product.quantity < 5 ? 'Low Stock' : 'In Stock'}
                        </span>
                          </td>
                          <td>
                            <div className="actions">
                              <button
                                  className="edit-button"
                                  onClick={() => openEditForm(product)}
                              >
                                Edit
                              </button>

                              <button
                                  className="delete-button"
                                  onClick={() => handleDelete(product.id)}
                              >
                                Delete
                              </button>
                            </div>
                          </td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
            )}
          </section>

          {showForm && (
              <div className="modal-backdrop">
                <div className="form-modal">
                  <div className="form-heading">
                    <div>
                      <h2>{editingId !== null ? 'Edit Product' : 'Add Product'}</h2>
                      <p>Enter the product information below.</p>
                    </div>

                    <button className="close-button" onClick={closeForm}>
                      ×
                    </button>
                  </div>

                  <form onSubmit={handleSubmit}>
                    <label>
                      Product Name
                      <input
                          name="name"
                          value={form.name}
                          onChange={handleInputChange}
                          maxLength="100"
                          required
                      />
                    </label>

                    <label>
                      Category
                      <input
                          name="category"
                          value={form.category}
                          onChange={handleInputChange}
                          maxLength="50"
                          required
                      />
                    </label>

                    <label>
                      Price
                      <input
                          type="number"
                          name="price"
                          value={form.price}
                          onChange={handleInputChange}
                          min="0.01"
                          step="0.01"
                          required
                      />
                    </label>

                    <label>
                      Quantity
                      <input
                          type="number"
                          name="quantity"
                          value={form.quantity}
                          onChange={handleInputChange}
                          min="0"
                          step="1"
                          required
                      />
                    </label>

                    <div className="form-actions">
                      <button
                          type="button"
                          className="secondary-button"
                          onClick={closeForm}
                      >
                        Cancel
                      </button>

                      <button
                          type="submit"
                          className="primary-button"
                          disabled={saving}
                      >
                        {saving
                            ? 'Saving...'
                            : editingId !== null
                                ? 'Update Product'
                                : 'Save Product'}
                      </button>
                    </div>
                  </form>
                </div>
              </div>
          )}
        </main>
      </div>
  )
}

function StatCard({ label, value, icon, warning }) {
  return (
      <div className="stat-card">
        <div>
          <p>{label}</p>
          <h2>{value}</h2>
        </div>
        <div className={`stat-icon ${warning ? 'warning' : ''}`}>
          {icon}
        </div>
      </div>
  )
}

export default App
