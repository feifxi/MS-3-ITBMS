const BASE_API = import.meta.env.VITE_BASE_API

export const fetchAllSaleItems = async () => {
    const res = await fetch(`${BASE_API}/v1/sale-items`)
    return await res.json()
}

export const fetchSaleItemById = async (id) => {
    const res = await fetch(`${BASE_API}/v1/sale-items/${id}`)
    if (!res.ok) throw new Error("Sale item not found")
    return await res.json()
  }