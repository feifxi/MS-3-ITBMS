const BASE_API = import.meta.env.VITE_BASE_API;

function cleanObject(obj) {
  return Object.fromEntries(
    Object.entries(obj).filter(
      ([_, value]) => value !== null && value !== "" && value !== undefined
    )
  );
}

export const fetchAllSaleItems = async (authStore) => {
  return await fetchWithAuth(`/v1/sale-items`, {
    method: "GET",
    headers: { "Content-Type": "application/json" }
  }, authStore)
};

export const fetchAllSaleItemsBySeller = async (authStore, page = 0, size = 10, sortField, sortDirection) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });

  if (sortField) {
    params.append('sortField', sortField);
    if (sortDirection) {
        params.append('sortDirection', sortDirection);
    }
  }

  return await fetchWithAuth(`/v2/sellers/${authStore.user.id}/sale-items?${params.toString()}`, {
    method: "GET",
    headers: { "Content-Type": "application/json" }
  }, authStore)
};

export const fetchAllSaleItemsV2 = async (page, size, filterBrands, filterPriceRange, filterStorageSizes, sortField, sortDirection , searchKeyword) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
    });
    
    if (filterBrands && filterBrands.length > 0) {
        filterBrands.forEach(brand => params.append('filterBrands', brand));
    }

    if (filterPriceRange && filterPriceRange.selectedRanges.length > 0) {
         const allRanges = filterPriceRange.selectedRanges
        if (allRanges.length > 0) {
            const minPrice = Math.min(...allRanges.map(r => r.min))
            const maxPrice = Math.max(...allRanges.map(r => r.max))
            params.append('priceLower', minPrice.toString())
            params.append('priceUpper', maxPrice.toString())
        }
    }

    if (filterStorageSizes && filterStorageSizes.length > 0) {
        filterStorageSizes.forEach(size => {
            if (typeof size === 'number') {
                params.append('storageSizes', size.toString())
                } else if (size === '1 TB') {
                params.append('storageSizes', '1024') 
            } else if (size === 'Not specified') {
                params.append('storageSizes', 'null') 
            }
        })
    }
    if (searchKeyword && searchKeyword.trim()) {
        params.append('searchKeyword', searchKeyword.trim());
    }
    
    if (sortField) {
        params.append('sortField', sortField);
        if (sortDirection) {
            params.append('sortDirection', sortDirection);
        }
    }
    
    return await fetch(`${BASE_API}/v2/sale-items?${params.toString()}`);
}

export const fetchAllStorageSizes = async () => {
  return await fetch(`${BASE_API}/v2/sale-items/storage-sizes`);
};

export const fetchPriceRanges = async () => {
  return await fetch(`${BASE_API}/v1/price-ranges`);
};

export const fetchSaleItemById = async (id) => {
  return await fetch(`${BASE_API}/v1/sale-items/${id}`);
};

export const createSaleItem = async (saleItem, authStore) => {
  return await fetchWithAuth(`/v1/sale-items`, {
    method: "POST",
    body: saleItem,
  }, authStore)
};

export const updateSaleItem = async (id, saleItem, authStore) => {
  return await fetchWithAuth(`/v1/sale-items/${id}`, {
    method: "PUT",
    body: saleItem,
  }, authStore)
};

export const deleteSaleItem = async (id, authStore) => {
  return await fetchWithAuth(`/v1/sale-items/${id}`, {
    method: "DELETE",
  }, authStore)
};

export const fetchAllBrands = async () => {
  return await fetch(`${BASE_API}/v1/brands`);
};

export const fetchBrandById = async (id) => {
  return await fetch(`${BASE_API}/v1/brands/${id}`);
};

export const deleteBrand = async (id) => {
  return await fetch(`${BASE_API}/v1/brands/${id}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
  });
};

export const createBrand = async (brand) => {
  return await fetch(`${BASE_API}/v1/brands`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(brand),
  });
};

export const updateBrand = async (id, brand) => {
  return await fetch(`${BASE_API}/v1/brands/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(brand),
  });
};

export const registerUser = async (userData) => {
  return await fetch(`${BASE_API}/v2/auth/registers`, {
    method: "POST",
    body: userData,
  });
};


export const verifyEmail = async (token) => {
  return await fetch(`${BASE_API}/v2/auth/verify-email`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      token: token,
    }),
  });
};

export const fetchUserProfile = async (authStore) => {
  return await fetchWithAuth(`/v2/users/${authStore.user.id}`, {}, authStore)
}

export const updateUserProfile = async (userData, authStore) => {
  return await fetchWithAuth(`/v2/users/${authStore.user.id}`, {
    method: "PUT",
    body: userData,
  }, authStore);
};

export const askChatbot = async (message, authStore) => {
  return await fetchWithAuth(`/v2/chatbot/ollama`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      message: message
    }),
  }, authStore);
}

// ==================   Fetch with Auth  ===========================

export async function fetchWithAuth(url, options = {}, authStore) {
  let accessToken = authStore.accessToken;
  // Add access token to headers
  options.headers = {
    ...options.headers,
    Authorization: `Bearer ${accessToken}`,
  };

  let response = await fetch(BASE_API + url, options);

  // If access token expired → try refresh
  if (response.status === 401) {
    try {
      accessToken = await refreshAccessToken(authStore);
    //   console.log("New : ", accessToken);

      // Retry request with new access token
      options.headers.Authorization = `Bearer ${accessToken}`;
      response = await fetch(BASE_API + url, options);
    } catch (e) {
      throw new Error("Session expired, please log in again.");
    }
  }
  return response;
}

export const refreshAccessToken = async (authStore) => {
  const res = await fetch(`${BASE_API}/v2/auth/refresh`, {
    method: "POST",
    credentials: "include", // ⬅️ send refresh token cookie
  });

  if (!res.ok) {
    authStore.accessToken = null
    authStore.user = null
    throw new Error("Invalid token or expires.");
  }

  const data = await res.json();
  const accessToken = data.accessToken
  authStore.accessToken = accessToken;
  // console.log("refresh token : ", data)
  return accessToken;
};
