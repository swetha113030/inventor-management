
import requests
import pandas as pd

API_URL = "http://localhost:8080/api/products"


def fetch_products():
    response = requests.get(API_URL, timeout=10)
    response.raise_for_status()
    return response.json()


def generate_report():
    products = fetch_products()

    df = pd.DataFrame(products)

    if df.empty:
        print("No products found.")
        return

    df["inventory_value"] = df["price"] * df["quantity"]

    total_products = len(df)
    total_stock = df["quantity"].sum()
    inventory_value = df["inventory_value"].sum()

    low_stock = df[df["quantity"] < 5]

    print("\n=== INVENTORY REPORT ===")
    print(f"Different products: {total_products}")
    print(f"Total units in stock: {total_stock}")
    print(f"Inventory value: {inventory_value:.2f}")

    print("\nProducts with fewer than 5 units:")
    for _, product in low_stock.iterrows():
        print(f"{product['name']}: {product['quantity']} units")

    # Export reports
    df.to_csv("inventory_report.csv", index=False)
    df.to_excel("inventory_report.xlsx", index=False)

    print("\nReports generated successfully!")
    print("Created: inventory_report.csv")
    print("Created: inventory_report.xlsx")


if __name__ == "__main__":
    generate_report()
