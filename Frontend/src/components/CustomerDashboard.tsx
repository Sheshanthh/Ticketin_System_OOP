import { useState, useEffect } from 'react';
import { 
  TextField, 
  Button, 
  Card, 
  CardContent, 
  Typography, 
  Checkbox, 
  FormControlLabel,
  Container,
  Box,
  Grid,
  Paper
} from '@mui/material';
import { Ticket, Crown, Calendar, Clock, MapPin } from 'lucide-react';

function CustomerDashboard() {
  const [events, setEvents] = useState<Record<string, string>>({});
  const [purchaseDetails, setPurchaseDetails] = useState({
    eventId: '',
    clientId: '',
    ticketCount: 1,
    purchaseRate: 1,
    isVip: false,
  });
  const [purchaseReady, setPurchaseReady] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState('');

  const fetchEvents = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/tickets/events');
      const data = await response.json();
      setEvents(data);
    } catch (error) {
      console.error('Error fetching events:', error);
    }
  };

  useEffect(() => {
    fetchEvents();
  }, []);

  const handlePreparePurchase = () => {
    const { eventId, clientId, ticketCount, purchaseRate } = purchaseDetails;

    if (!eventId || !clientId || ticketCount <= 0 || purchaseRate <= 0) {
      alert('Please fill in all fields with valid values!');
      return;
    }

    alert('Purchase details prepared! Click "Confirm Purchase" to complete the transaction.');
    setPurchaseReady(true);
  };

  const handleConfirmPurchase = async () => {
    if (!purchaseReady) return;

    try {
      const response = await fetch('http://localhost:8080/api/tickets/purchase', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          eventId: purchaseDetails.eventId,
          clientId: purchaseDetails.clientId,
          ticketCount: Number(purchaseDetails.ticketCount),
          purchaseRate: Number(purchaseDetails.purchaseRate),
          isVip: Boolean(purchaseDetails.isVip),
        }),
      });
      
      if (response.ok) {
        const result = await response.text();
        alert(result);
        fetchEvents();
        setPurchaseReady(false);
        setPurchaseDetails({
          eventId: '',
          clientId: '',
          ticketCount: 1,
          purchaseRate: 1,
          isVip: false,
        });
        setSelectedEvent('');
      } else {
        const errorText = await response.text();
        alert(`Error purchasing tickets: ${errorText}`);
      }
    } catch (error) {
      console.error('Error:', error);
      alert('Error purchasing tickets');
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Ticket />
        TryBooking
      </Typography>
      <Typography variant="subtitle1" color="text.secondary" gutterBottom>
        Book your tickets with ease
      </Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, mb: 4 }}>
            <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Ticket size={24} />
              Available Shows
            </Typography>
            <Grid container spacing={3}>
              {Object.entries(events).map(([eventId, ticketCount]) => (
                <Grid item xs={12} sm={6} key={eventId}>
                  <Card 
                    sx={{ 
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      '&:hover': { transform: 'translateY(-4px)', boxShadow: 4 },
                      border: selectedEvent === eventId ? 2 : 0,
                      borderColor: 'primary.main'
                    }}
                    onClick={() => {
                      setSelectedEvent(eventId);
                      setPurchaseDetails(prev => ({ ...prev, eventId }));
                    }}
                  >
                    <Box sx={{ position: 'relative' }}>
                      <img
                        src={`https://source.unsplash.com/800x400/?concert,event&${eventId}`}
                        alt={eventId}
                        style={{ width: '100%', height: '200px', objectFit: 'cover' }}
                      />
                      <Box
                        sx={{
                          position: 'absolute',
                          top: 8,
                          right: 8,
                          bgcolor: 'rgba(0,0,0,0.8)',
                          color: 'white',
                          px: 2,
                          py: 0.5,
                          borderRadius: 'full',
                        }}
                      >
                        {ticketCount} seats
                      </Box>
                    </Box>
                    <CardContent>
                      <Typography variant="h6" gutterBottom>
                        {eventId}
                      </Typography>
                      <Box sx={{ color: 'text.secondary' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Calendar size={16} />
                          <span>Today</span>
                        </Box>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Clock size={16} />
                          <span>7:00 PM</span>
                        </Box>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <MapPin size={16} />
                          <span>Main Hall</span>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h5" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Crown size={24} />
                Purchase Tickets
              </Typography>
              <TextField
                fullWidth
                label="Event ID"
                value={purchaseDetails.eventId}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPurchaseDetails({ ...purchaseDetails, eventId: e.target.value })}
                margin="normal"
              />
              <TextField
                fullWidth
                label="Client ID"
                value={purchaseDetails.clientId}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPurchaseDetails({ ...purchaseDetails, clientId: e.target.value })}
                margin="normal"
              />
              <TextField
                fullWidth
                type="number"
                label="Number of Tickets"
                value={purchaseDetails.ticketCount}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPurchaseDetails({ ...purchaseDetails, ticketCount: Number(e.target.value) })}
                margin="normal"
                inputProps={{ min: 1 }}
              />
              <TextField
                fullWidth
                type="number"
                label="Purchase Rate (seconds)"
                value={purchaseDetails.purchaseRate}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPurchaseDetails({ ...purchaseDetails, purchaseRate: Number(e.target.value) })}
                margin="normal"
                inputProps={{ min: 1 }}
              />
              <FormControlLabel
                control={
                  <Checkbox
                    checked={purchaseDetails.isVip}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPurchaseDetails({ ...purchaseDetails, isVip: e.target.checked })}
                  />
                }
                label="VIP"
              />
              <Button
                variant="contained"
                color="primary"
                fullWidth
                sx={{ mt: 2 }}
                onClick={handlePreparePurchase}
              >
                Prepare Purchase
              </Button>
              {purchaseReady && (
                <Button
                  variant="contained"
                  color="secondary"
                  fullWidth
                  sx={{ mt: 2 }}
                  onClick={handleConfirmPurchase}
                >
                  Confirm Purchase
                </Button>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
}

export default CustomerDashboard;
