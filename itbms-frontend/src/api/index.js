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

// export const fetchAllSaleItemsV2 = async (page, size, filterBrands,filterPriceRange, 
//   filterStorageSizes,  sortField, sortDirection) => {
//     const brands = '&filterBrands=' + filterBrands.reduce((allBrand, brand) => allBrand + ',' + brand, '')
//     const sort = `&sortField=${sortField}${sortField === 'createdOn' ? '' : '&sortDirection='+sortDirection}` 
//     const query = `?page=${page}&size=${size}` + (filterBrands.length > 0 ? brands : '') + sort
//     const price = filterPriceRange   ? `&priceMin=${filterPriceRange.lower}&priceMax=${filterPriceRange.upper}`   : ''
//     const storage = filterStorageSizes.length > 0  ? '&filterStorageSizes=' + filterStorageSizes.join(',') : ''
//     // console.log(query)
//     return await fetch(`${BASE_API}/v2/sale-items` + query)
// }

export const fetchAllSaleItemsV2 = async (page, size, filterBrands, filterPriceOptions, 
  filterStorageSizes, sortField, sortDirection) => {
    const brands = '&filterBrands=' + filterBrands.reduce((allBrand, brand) => allBrand + ',' + brand, '')
    const sort = `&sortField=${sortField}${sortField === 'createdOn' ? '' : '&sortDirection='+sortDirection}` 
    let query = `?page=${page}&size=${size}` + (filterBrands.length > 0 ? brands : '') + sort
    
    // แก้ไข price parameter ให้รองรับ multiple price ranges
    if (filterPriceOptions.length > 0) {
      const priceRanges = filterPriceOptions.map(option => `${option.lower}-${option.upper}`).join(',')
      query += `&priceRanges=${priceRanges}`
    }
    
    const storage = filterStorageSizes.length > 0 ? '&filterStorageSizes=' + filterStorageSizes.join(',') : ''
    
    return await fetch(`${BASE_API}/v2/sale-items${query}${storage}`)
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
