/* eslint-disable react-hooks/exhaustive-deps */
import React, { useState, useEffect } from 'react';
import { Ticket, Users, BarChart, PlusCircle, AlertCircle, CheckCircle2, X, Star } from 'lucide-react';
import { api } from '../api';

export function AdminDashboard() {
  const [events, setEvents] = useState<Record<string, string>>({});
  const [activeTab, setActiveTab] = useState<'tickets' | 'vip' | 'analytics'>('tickets');
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  const [ticketForm, setTicketForm] = useState({
    numoftickets: 0,
    eventID: '',
    vendorname: '',
    releaserate: 1,
  });

  const [vipForm, setVipForm] = useState({
    customerName: '',
  });

  useEffect(() => {
    loadEvents();
  }, []);

  useEffect(() => {
    if (toast) {
      const timer = setTimeout(() => setToast(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [toast]);

  const loadEvents = async () => {
    try {
      const data = await api.getAllEvents();
      setEvents(data);
    } catch (error) {
      showToast('Failed to load events', 'error');
    }
  };

  const showToast = (message: string, type: 'success' | 'error') => {
    setToast({ message, type });
  };

  const handleAddTickets = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.addTickets({
        ...ticketForm,
        eventname: ticketForm.eventID, // Use eventID as eventname
      });
      showToast(response, 'success');
      loadEvents();
      setTicketForm({
        numoftickets: 0,
        eventID: '',
        vendorname: '',
        releaserate: 1,
      });
    } catch (error) {
      showToast('Failed to add tickets', 'error');
    }
  };

  const handleRegisterVIP = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.registerVIP(vipForm.customerName);
      showToast(response, 'success');
      setVipForm({ customerName: '' });
    } catch (error) {
      showToast('Failed to register VIP customer', 'error');
    }
  };

  return (
    <div className="min-h-screen bg-black text-white">
      {/* Toast Notification */}
      {toast && (
        <div
          className={`fixed top-4 right-4 z-50 p-4 rounded-lg shadow-2xl animate-slide-in flex items-center space-x-3 ${
            toast.type === 'success' ? 'bg-green-900' : 'bg-red-900'
          } glass-effect`}
        >
          {toast.type === 'success' ? (
            <CheckCircle2 className="h-5 w-5" />
          ) : (
            <AlertCircle className="h-5 w-5" />
          )}
          <span className="font-medium">{toast.message}</span>
          <button
            onClick={() => setToast(null)}
            className="ml-2 hover:opacity-75 transition-opacity"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      )}

      {/* Navigation */}
      <nav className="bg-zinc-900/50 backdrop-blur-lg border-b border-white/5">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Ticket className="h-8 w-8 text-white" />
                <span className="text-2xl font-bold tracking-tight bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                  TryBooking
                </span>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Star className="h-4 w-4 text-yellow-500" />
                <span className="text-sm font-medium text-zinc-400">Premium Admin Portal</span>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 animate-fade-in">
        {/* Tabs */}
        <div className="mb-8 bg-zinc-900/50 backdrop-blur-lg rounded-xl overflow-hidden gradient-border">
          <nav className="flex">
            {[
              { id: 'tickets', label: 'Ticket Management', icon: Ticket },
              { id: 'vip', label: 'VIP Management', icon: Users },
              { id: 'analytics', label: 'Analytics', icon: BarChart },
            ].map(({ id, label, icon: Icon }) => (
              <button
                key={id}
                onClick={() => setActiveTab(id as any)}
                className={`flex-1 py-4 px-4 flex items-center justify-center space-x-2 transition-all duration-300 ${
                  activeTab === id
                    ? 'bg-gradient-to-b from-white to-gray-100 text-black'
                    : 'text-zinc-400 hover:text-white hover:bg-white/5'
                }`}
              >
                <Icon className="h-5 w-5" />
                <span className="font-medium">{label}</span>
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        <div className="bg-zinc-900/50 backdrop-blur-lg rounded-xl gradient-border animate-scale-in">
          {activeTab === 'tickets' && (
            <div className="p-8">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
                <div className="space-y-8">
                  <div className="space-y-2">
                    <h2 className="text-2xl font-semibold bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                      Add New Tickets
                    </h2>
                    <p className="text-zinc-400">Create and manage event tickets</p>
                  </div>
                  <form onSubmit={handleAddTickets} className="space-y-6">
                    {[
                      { label: 'Number of Tickets', key: 'numoftickets', type: 'number' },
                      { label: 'Event ID', key: 'eventID', type: 'text' },
                      { label: 'Vendor Name', key: 'vendorname', type: 'text' },
                      { label: 'Release Rate (seconds)', key: 'releaserate', type: 'number' },
                    ].map(({ label, key, type }) => (
                      <div key={key}>
                        <label className="block text-sm font-medium text-zinc-300 mb-2">
                          {label}
                        </label>
                        <input
                          type={type}
                          value={ticketForm[key as keyof typeof ticketForm]}
                          onChange={(e) =>
                            setTicketForm({
                              ...ticketForm,
                              [key]: type === 'number' ? parseInt(e.target.value) : e.target.value,
                            })
                          }
                          className="premium-input w-full rounded-lg px-4 py-3 focus:outline-none text-white placeholder-zinc-500"
                          required
                          min={type === 'number' ? "1" : undefined}
                        />
                      </div>
                    ))}
                    <button
                      type="submit"
                      className="premium-button w-full py-3 rounded-lg font-medium text-black flex items-center justify-center space-x-2 hover-scale"
                    >
                      <PlusCircle className="h-5 w-5" />
                      <span>Add Tickets</span>
                    </button>
                  </form>
                </div>

                <div className="space-y-8">
                  <div className="space-y-2">
                    <h2 className="text-2xl font-semibold bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                      Current Events
                    </h2>
                    <p className="text-zinc-400">Monitor active event tickets</p>
                  </div>
                  <div className="space-y-4">
                    {Object.entries(events).map(([eventId, details]) => (
                      <div
                        key={eventId}
                        className="gradient-border bg-black/50 backdrop-blur-lg p-6 rounded-lg hover:border-white/20 transition-all duration-200 hover-scale"
                      >
                        <h3 className="font-medium text-lg bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                          {eventId}
                        </h3>
                        <p className="text-zinc-400 mt-2">{details}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'vip' && (
            <div className="p-8">
              <div className="max-w-lg mx-auto space-y-8">
                <div className="space-y-2 text-center">
                  <h2 className="text-2xl font-semibold bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                    Register VIP Customer
                  </h2>
                  <p className="text-zinc-400">Create exclusive VIP memberships</p>
                </div>
                <form onSubmit={handleRegisterVIP} className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-zinc-300 mb-2">
                      Customer Name
                    </label>
                    <input
                      type="text"
                      value={vipForm.customerName}
                      onChange={(e) => setVipForm({ customerName: e.target.value })}
                      className="premium-input w-full rounded-lg px-4 py-3 focus:outline-none text-white"
                      required
                    />
                  </div>
                  <button
                    type="submit"
                    className="premium-button w-full py-3 rounded-lg font-medium text-black flex items-center justify-center space-x-2 hover-scale"
                  >
                    <Users className="h-5 w-5" />
                    <span>Register VIP</span>
                  </button>
                </form>
              </div>
            </div>
          )}

          {activeTab === 'analytics' && (
            <div className="p-8">
              <div className="text-center py-16 space-y-4">
                <div className="shimmer w-16 h-16 rounded-full mx-auto flex items-center justify-center">
                  <BarChart className="h-8 w-8 text-zinc-600" />
                </div>
                <h3 className="text-2xl font-semibold bg-gradient-to-r from-white to-gray-400 bg-clip-text text-transparent">
                  Analytics Dashboard
                </h3>
                <p className="text-zinc-400 max-w-md mx-auto">
                  Comprehensive analytics and insights will be available in the upcoming release.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}