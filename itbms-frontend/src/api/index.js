const BASE_API = import.meta.env.VITE_BASE_API

export const fetchAllSaleItems = async () => {
    return await fetch(`${BASE_API}/v1/sale-items`)
}

export const fetchSaleItemById = async (id) => {
    return await fetch(`${BASE_API}/v1/sale-items/${id}`)
}

export const createSaleItem = async (saleItem) => {
    return await fetch(`${BASE_API}/v1/sale-items`,{
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(saleItem),
    })
}

export const updateSaleItem = async (id, saleItem) => {
    return await fetch(`${BASE_API}/v1/sale-items/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(saleItem),
    })
}

export const deleteSaleItem = async (id) => {
    return await fetch(`${BASE_API}/v1/sale-items/${id}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
    })
}

export const fetchAllBrands = async () => {
    return await fetch(`${BASE_API}/v1/brands`)
}

export const deleteBrand = async (id) => {
    return await fetch(`${BASE_API}/v1/brands/${id}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
    })
}
