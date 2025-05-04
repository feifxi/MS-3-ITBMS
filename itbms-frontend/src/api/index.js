const BASE_API = import.meta.env.VITE_BASE_API

export const fetchAllSaleItems = async () => {
    return await fetch(`${BASE_API}/v1/sale-items`)
}

export const fetchSaleItemById = async (id) => {
    return await fetch(`${BASE_API}/v1/sale-items/${id}`)
  }
  