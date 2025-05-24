const BASE_API = import.meta.env.VITE_BASE_API

function cleanObject(obj) {
  return Object.fromEntries(
    Object.entries(obj)
      .filter(([_, value]) => value !== null && value !== '' && value !== undefined)
  );
}

export const fetchAllSaleItems = async () => {
    return await fetch(`${BASE_API}/v1/sale-items`)
}

export const fetchAllSaleItemsV2 = async (page, size, filterBrands, sortField, sortDirection) => {
    const brands = '&filterBrands=' + filterBrands.reduce((allBrand, brand) => allBrand + ',' + brand, '')
    const sort = `&sortField=${sortField}${sortField === 'createdOn' ? '' : '&sortDirection='+sortDirection}` 
    const query = `?page=${page}&size=${size}` + (filterBrands.length > 0 ? brands : '') + sort
    console.log(query)
    return await fetch(`${BASE_API}/v2/sale-items` + query)
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

export const fetchBrandById = async (id) => {
    return await fetch(`${BASE_API}/v1/brands/${id}`)
}

export const deleteBrand = async (id) => {
    return await fetch(`${BASE_API}/v1/brands/${id}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
    })
}

export const createBrand = async (brand) => {
  return await fetch(`${BASE_API}/v1/brands`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(brand),
  })
}


export const updateBrand = async (id, brand) => {
  return await fetch(`${BASE_API}/v1/brands/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(brand),
  })
}
