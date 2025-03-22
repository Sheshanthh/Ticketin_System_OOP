const API_BASE_URL = 'http://localhost:8080/api/tickets';

export const api = {
  // Admin endpoints
  addTickets: async (data: {
    numoftickets: number;
    eventID: string;
    eventname: string;
    vendorname: string;
    releaserate: number;
  }) => {
    const params = new URLSearchParams(data as any);
    const response = await fetch(`${API_BASE_URL}/add-with-rate?${params}`, {
      method: 'POST',
    });
    return response.text();
  },

  registerVIP: async (customerName: string) => {
    const response = await fetch(`${API_BASE_URL}/register-vip?customerName=${customerName}`, {
      method: 'POST',
    });
    return response.text();
  },

  setRetrievalRate: async (rate: number) => {
    const response = await fetch(`${API_BASE_URL}/set-customer-retrieval-rate?rate=${rate}`, {
      method: 'POST',
    });
    return response.text();
  },

  getAllEvents: async () => {
    const response = await fetch(`${API_BASE_URL}/events`);
    return response.json();
  },

  // Customer endpoints
  purchaseTickets: async (data: {
    eventId: string;
    clientId: string;
    ticketCount: number;
    purchaseRate: number;
    isVip: boolean;
  }) => {
    const response = await fetch(`${API_BASE_URL}/purchase`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return response.text();
  },

  addPriorityCustomer: async (data: {
    customerId: string;
    priority: number;
    eventId: string;
  }) => {
    const params = new URLSearchParams(data as any);
    const response = await fetch(`${API_BASE_URL}/add-priority-customer?${params}`, {
      method: 'POST',
    });
    return response.text();
  },
};