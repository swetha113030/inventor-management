const API_BASE_URL = `${import.meta.env.VITE_API_BASE_URL || ''}/api/products`

async function request(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    })

    if (!response.ok) {
        let message = `Request failed: ${response.status}`

        try {
            const error = await response.json()
            message = error.message || error.error || message
        } catch {
            // Keep the default error message if the response is not JSON.
        }

        throw new Error(message)
    }

    if (response.status === 204) {
        return null
    }

    return response.json()
}

export const productService = {
    getAll: () => request(API_BASE_URL),

    getById: (id) => request(`${API_BASE_URL}/${id}`),

    create: (product) =>
        request(API_BASE_URL, {
            method: 'POST',
            body: JSON.stringify(product),
        }),

    update: (id, product) =>
        request(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            body: JSON.stringify(product),
        }),

    delete: (id) =>
        request(`${API_BASE_URL}/${id}`, {
            method: 'DELETE',
        }),

    getSummary: () => request(`${API_BASE_URL}/inventory-summary`),

    getLowStock: (threshold = 5) =>
        request(`${API_BASE_URL}/low-stock?threshold=${threshold}`),
}
