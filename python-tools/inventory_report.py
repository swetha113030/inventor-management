"""Read inventory data from the Java API and print a simple report."""

import json
from decimal import Decimal
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError

BASE_URL = "http://localhost:8080/api/products"


def get_products():
    """Return all products from the Java API."""
    with urlopen(BASE_URL, timeout=10) as response:
        return json.loads(response.read().decode("utf-8"))


def add_product(name, category, price, quantity):
    """Create a product through the Java API."""
    data = {
        "name": name,
        "category": category,
        "price": str(price),
        "quantity": quantity,
    }
    request = Request(
        BASE_URL,
        data=json.dumps(data).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urlopen(request, timeout=10) as response:
        return json.loads(response.read().decode("utf-8"))


def print_inventory_report(products):
    """Print counts, value and low-stock products."""
    total_units = sum(int(product["quantity"]) for product in products)
    total_value = sum(
        Decimal(str(product["price"])) * int(product["quantity"])
        for product in products
    )
    low_stock = [p for p in products if int(p["quantity"]) < 5]

    print("\\n=== INVENTORY REPORT ===")
    print(f"Different products: {len(products)}")
    print(f"Total units in stock: {total_units}")
    print(f"Inventory value: {total_value:.2f}")
    print("\\nProducts with fewer than 5 units:")

    if not low_stock:
        print("No low-stock products.")
    else:
        for product in low_stock:
            print(f'{product["name"]}: {product["quantity"]} units')


if __name__ == "__main__":
    try:
        products = get_products()
        print_inventory_report(products)
    except HTTPError as error:
        print(f"API returned HTTP error: {error.code}")
    except URLError:
        print("Cannot reach Java API. Is Spring Boot running on port 8080?")
    except (ValueError, KeyError) as error:
        print(f"Unexpected API response: {error}")
