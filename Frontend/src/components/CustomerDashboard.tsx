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
  Paper,
  useTheme,
  alpha
} from '@mui/material';
import { Ticket, Crown, Calendar, Clock, MapPin, Search } from 'lucide-react';
import { motion } from 'framer-motion';

function CustomerDashboard() {
  const theme = useTheme();
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
  const [searchQuery, setSearchQuery] = useState('');

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
    <Box sx={{ 
      minHeight: '100vh',
      background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.1)} 0%, ${alpha(theme.palette.secondary.main, 0.1)} 100%)`,
      position: 'relative',
      overflow: 'hidden'
    }}>
      {/* Parallax Background Elements */}
      <Box sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        height: '100vh',
        zIndex: 0,
        opacity: 0.1,
        background: 'url("https://source.unsplash.com/1920x1080/?concert,stage") center/cover no-repeat fixed',
        transform: 'translateZ(-1px) scale(2)',
      }} />

      <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1, py: 4 }}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Typography 
            variant="h2" 
            component="h1" 
            gutterBottom 
            sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 2,
              fontWeight: 700,
              background: `linear-gradient(45deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              mb: 1
            }}
          >
            <Ticket size={48} />
            TryBooking
          </Typography>
          <Typography 
            variant="h5" 
            color="text.secondary" 
            gutterBottom
            sx={{ mb: 4 }}
          >
            Experience the magic of live events
          </Typography>
        </motion.div>

        <Grid container spacing={4}>
          <Grid item xs={12} md={8}>
            <Paper 
              elevation={0}
              sx={{ 
                p: 3, 
                mb: 4,
                background: alpha(theme.palette.background.paper, 0.9),
                backdropFilter: 'blur(10px)',
                borderRadius: 4,
                border: `1px solid ${alpha(theme.palette.divider, 0.1)}`
              }}
            >
              <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
                <Typography variant="h5" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Ticket size={24} />
                  Available Shows
                </Typography>
                <TextField
                  size="small"
                  placeholder="Search events..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  InputProps={{
                    startAdornment: <Search size={20} style={{ marginRight: 8, color: theme.palette.text.secondary }} />,
                  }}
                  sx={{ ml: 'auto', maxWidth: 300 }}
                />
              </Box>
              <Grid container spacing={3}>
                {Object.entries(events)
                  .filter(([eventId]) => eventId.toLowerCase().includes(searchQuery.toLowerCase()))
                  .map(([eventId, ticketCount]) => (
                    <Grid item xs={12} sm={6} key={eventId}>
                      <motion.div
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                      >
                        <Card 
                          sx={{ 
                            cursor: 'pointer',
                            transition: 'all 0.3s',
                            borderRadius: 3,
                            overflow: 'hidden',
                            border: selectedEvent === eventId ? 2 : 0,
                            borderColor: 'primary.main',
                            boxShadow: selectedEvent === eventId ? 4 : 1,
                            '&:hover': { 
                              transform: 'translateY(-8px)',
                              boxShadow: 8
                            }
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
                              style={{ 
                                width: '100%', 
                                height: '200px', 
                                objectFit: 'cover',
                                filter: 'brightness(0.9)'
                              }}
                            />
                            <Box
                              sx={{
                                position: 'absolute',
                                top: 12,
                                right: 12,
                                bgcolor: alpha(theme.palette.primary.main, 0.9),
                                color: 'white',
                                px: 2,
                                py: 0.5,
                                borderRadius: '20px',
                                backdropFilter: 'blur(4px)',
                                fontWeight: 600
                              }}
                            >
                              {ticketCount} seats left
                            </Box>
                          </Box>
                          <CardContent>
                            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
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
                      </motion.div>
                    </Grid>
                  ))}
              </Grid>
            </Paper>
          </Grid>

          <Grid item xs={12} md={4}>
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.5, delay: 0.2 }}
            >
              <Card sx={{ 
                borderRadius: 3,
                background: alpha(theme.palette.background.paper, 0.9),
                backdropFilter: 'blur(10px)',
                border: `1px solid ${alpha(theme.palette.divider, 0.1)}`
              }}>
                <CardContent>
                  <Typography 
                    variant="h5" 
                    gutterBottom 
                    sx={{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      gap: 1,
                      fontWeight: 600,
                      mb: 3
                    }}
                  >
                    <Crown size={24} />
                    Purchase Tickets
                  </Typography>
                  <TextField
                    fullWidth
                    label="Event ID"
                    value={purchaseDetails.eventId}
                    onChange={(e) => setPurchaseDetails({ ...purchaseDetails, eventId: e.target.value })}
                    margin="normal"
                    sx={{ mb: 2 }}
                  />
                  <TextField
                    fullWidth
                    label="Client ID"
                    value={purchaseDetails.clientId}
                    onChange={(e) => setPurchaseDetails({ ...purchaseDetails, clientId: e.target.value })}
                    margin="normal"
                    sx={{ mb: 2 }}
                  />
                  <TextField
                    fullWidth
                    type="number"
                    label="Number of Tickets"
                    value={purchaseDetails.ticketCount}
                    onChange={(e) => setPurchaseDetails({ ...purchaseDetails, ticketCount: Number(e.target.value) })}
                    margin="normal"
                    inputProps={{ min: 1 }}
                    sx={{ mb: 2 }}
                  />
                  <TextField
                    fullWidth
                    type="number"
                    label="Purchase Rate (seconds)"
                    value={purchaseDetails.purchaseRate}
                    onChange={(e) => setPurchaseDetails({ ...purchaseDetails, purchaseRate: Number(e.target.value) })}
                    margin="normal"
                    inputProps={{ min: 1 }}
                    sx={{ mb: 2 }}
                  />
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={purchaseDetails.isVip}
                        onChange={(e) => setPurchaseDetails({ ...purchaseDetails, isVip: e.target.checked })}
                      />
                    }
                    label="VIP"
                    sx={{ mb: 2 }}
                  />
                  <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ 
                      mt: 2,
                      py: 1.5,
                      borderRadius: 2,
                      textTransform: 'none',
                      fontSize: '1.1rem',
                      fontWeight: 600
                    }}
                    onClick={handlePreparePurchase}
                  >
                    Prepare Purchase
                  </Button>
                  {purchaseReady && (
                    <Button
                      variant="contained"
                      color="secondary"
                      fullWidth
                      sx={{ 
                        mt: 2,
                        py: 1.5,
                        borderRadius: 2,
                        textTransform: 'none',
                        fontSize: '1.1rem',
                        fontWeight: 600
                      }}
                      onClick={handleConfirmPurchase}
                    >
                      Confirm Purchase
                    </Button>
                  )}
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}

export default CustomerDashboard;
