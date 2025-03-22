export interface Event {
  eventId: string;
  eventName: string;
  ticketCount: number;
}

export interface VIPCustomer {
  customerId: string;
  customerName: string;
}

export interface PriorityCustomer {
  customerId: string;
  priority: number;
  eventId: string;
}