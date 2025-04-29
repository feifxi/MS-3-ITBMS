const BASE_API = import.meta.env.VITE_BASE_API

export const fetchAllSaleItems = async () => {
    const res = await fetch(`${BASE_API}/v1/sale-items`)
    return await res.json()
}