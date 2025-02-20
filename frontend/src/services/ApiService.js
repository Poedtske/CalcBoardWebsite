import {request} from "../AxiosConfig";

//handles all apicalls, uses AxiosConfig

const baseURL = process.env.REACT_APP_API_BASE_URL;


const apiUrl = `${baseURL}/api/public/products`;
const apiUrlEvent = `${baseURL}/api/public/events`;

//Products API
export const getProducts = async () => {
  try {
    const response = await request('GET','/api/public/products',false)
    let products=response.data;
    if (!Array.isArray(products)) {
      console.error('Fetched products are not an array:', products);
      return [];
    }
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error.response || error);
    throw error;
  }
};

export const getProductImg = async (id) => {
  try {
    const response = await request('GET',`/api/public/products/${id}/image`,false,false,false,true)
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error.response || error);
    throw error;
  }
};

export const createProduct = async (product) => {
  try {
    const response = await request('POST','/api/admin/products',product,true,true)
    return response.data;
  } catch (error) {
    console.error('Error creating product:', error.response || error);
    throw error;
  }
};

export const getProductById = async (id) => {
  try {
    const response = await request('GET',`/api/public/products/${id}`,false)
    return response.data;
  } catch (error) {
    console.error(`Error fetching product with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const adminGetProductById = async (id) => {
  try {
    const response = await request('GET',`/api/admin/products/${id}`,null,true)
    return response.data;
  } catch (error) {
    console.error(`Error fetching product with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const updateProductByIdWithImg = async (id, product) => {
  try {
    const response = await request('PUT',`/api/admin/products/${id}/img`,product,true,true)
    return response.data;
  } catch (error) {
    console.error(`Error updating product with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const updateProductByIdWithoutImg = async (id, product) => {
  try {
    const response = await request('PUT',`/api/admin/products/${id}`,product,true,true)
    return response.data;
  } catch (error) {
    console.error(`Error updating product with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const deleteProductById = async (id) => {
  try {
    const response = await request('DELETE',`/api/admin/products/${id}`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error deleting product with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const getCategories = async () => {
  try {
    
    const response = await request('GET','/api/public/categories',false,false)
    const types = response.data;

    if (Array.isArray(types)) {
      return types
    } else {
      console.error('Fetched events are not an array:', types);
      return [];
    }
  } catch (error) {
    console.error('Error fetching events:', error.response || error);
    throw error;
  }
};

// Events API

export const createEvent = async (event) => {
  try {
    const response = await request('POST','/api/admin/events',event,true, true)
    return response.data;
  } catch (error) {
    console.error('Error creating product:', error.response || error);
    throw error;
  }
};

export const getEvents = async () => {
  try {
    const response = await request('GET', '/api/public/events', false, false);
    const events = response.data;

    console.log(events);

    if (!Array.isArray(events)) {
      console.error('Fetched events are not an array:', events);
      return [];
    }

    // Helper function to format the date
    const formatDate = (dateTimeString) => {
      if (!dateTimeString) {
        return { day: "--", month: "N/A" };
      }

      const date = new Date(dateTimeString.replace(" ", "T"));

      if (isNaN(date.getTime())) {
        console.error("Invalid date format:", dateTimeString);
        return { day: "--", month: "N/A" };
      }

      const day = String(date.getDate()).padStart(2, "0");
      const month = date.toLocaleString("en", { month: "short" }).toUpperCase();
      return { day, month };
    };

    // Helper function to process event dates
    const processDates = (event) => {
      if (event.startTime && event.endTime) {
        const { day, month } = formatDate(event.startTime);
        return { mainDate: { day, month }, extraDates: [] };
      }

      if (event.dates && event.dates.length > 0) {
        const uniqueDays = new Set();
        const extraDates = [];
        let mainDate = null;

        event.dates.forEach((date) => {
          const fullDateTime = `${date.date}T${date.startTime}`;
          const { day, month } = formatDate(fullDateTime);
          const dateKey = `${day}-${month}`;
          if (!uniqueDays.has(dateKey)) {
            uniqueDays.add(dateKey);
            if (!mainDate) {
              mainDate = { day, month };
            } else {
              extraDates.push({ day, month });
            }
          }
        });

        return { mainDate, extraDates };
      }

      return { mainDate: { day: "--", month: "N/A" }, extraDates: [] };
    };

    // Process each event to ensure proper dates and sorting
    const formattedEvents = events.map((event) => {
      const { mainDate, extraDates } = processDates(event);
      return {
        ...event,
        mainDate,
        extraDates,
      };
    });

    // Sort events by startTime or fallback to first date in `dates`
    const sortedEvents = formattedEvents.sort((a, b) => {
      const dateA = a.startTime
        ? new Date(a.startTime.replace(" ", "T")).getTime()
        : a.dates && a.dates[0]
        ? new Date(`${a.dates[0].date}T${a.dates[0].startTime}`).getTime()
        : Number.MAX_VALUE;

      const dateB = b.startTime
        ? new Date(b.startTime.replace(" ", "T")).getTime()
        : b.dates && b.dates[0]
        ? new Date(`${b.dates[0].date}T${b.dates[0].startTime}`).getTime()
        : Number.MAX_VALUE;

      return dateA - dateB; // Ascending order
    });

    return sortedEvents;
  } catch (error) {
    console.error('Error fetching events:', error.response || error);
    throw error;
  }
};


export const getEventTypes = async () => {
  try {
    
    const response = await request('GET','/api/public/eventTypes',false)
    const types = response.data;

    if (Array.isArray(types)) {
      return types
    } else {
      console.error('Fetched events are not an array:', types);
      return [];
    }
  } catch (error) {
    console.error('Error fetching events:', error.response || error);
    throw error;
  }
};

export const updateEventByIdWithImg = async (id, event) => {
  try {
    const response = await request('PUT',`/api/admin/events/${id}/img`,event,true,true)
    return response.data;
  } catch (error) {
    console.error(`Error updating event with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const updateEventByIdWithoutImg = async (id, event) => {
  try {
    const response = await request('PUT',`/api/admin/events/${id}`,event,true,true)
    return response.data;
  } catch (error) {
    console.error(`Error updating event with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const getEventById = async (id) => {
  try {
    const response = await request('GET',`/api/public/events/${id}`,false)
    return response.data;
  } catch (error) {
    console.error(`Error fetching event with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const deleteEventById = async (id) => {
  try {
    const response = await request('DELETE',`/api/admin/events/${id}`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error deleting event with ID ${id}:`, error.response || error);
    throw error;
  }
};

export const addTicketToCart= async(ticket)=>{
  try {
    const response = await request('POST',`/api/secure/tickets`,ticket,true);
    return response;
  } catch (error) {
    console.error(`Error creating ticket ${ticket}:`, error.response || error);
    throw error;
  }
}

export const addProductToCart= async(product)=>{
  try {
    const response = await request('POST',`/api/secure/products`,product,true);
    return response;
  } catch (error) {
    console.error(`Error adding product ${product}:`, error.response || error);
    throw error;
  }
}

export const getCart= async()=>{
  try {
    const response = await request('GET',`/api/secure/cart`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error getting cart:`, error.response || error);
    throw error;
  }
}

export const clearCart= async()=>{
  try {
    const response = await request('DELETE',`/api/secure/cart`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error getting cart:`, error.response || error);
    throw error;
  }
}


export const removeProductFromCart= async(id)=>{
  try {
    const response = await request('DELETE',`/api/secure/cart/products/${id}`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error removing product:`, error.response || error);
    throw error;
  }
}

export const removeTicketFromCart= async(id)=>{
  try {
    const response = await request('DELETE',`/api/secure/cart/tickets/${id}`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error removing ticket:`, error.response || error);
    throw error;
  }
}

export const pay= async(products)=>{
  try {
    const response = await request('PUT',`/api/secure/pay`,products,true);
    return response.data;
  } catch (error) {
    console.error(`Error removing ticket:`, error.response || error);
    throw error;
  }
}

export const manageAvailabilityProduct= async(id)=>{
  try {
    const response = await request('PUT',`/api/admin/products/${id}/availability`,null,true);
    return response.data;
  } catch (error) {
    console.error(`Error managing availability:`, error.response || error);
    throw error;
  }
}

export const getEventImg = async (id) => {
  try {
    const response = await request('GET',`/api/public/events/${id}/image`,false,false,false,true)
    return response.data;
  } catch (error) {
    console.error('Error fetching events:', error.response || error);
    return null;
  }
};
