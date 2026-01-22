/**
 * This file was created by Claude Sonnet 4.5
 */
import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { 
  FiUser, 
  FiMail, 
  FiCalendar, 
  FiShield, 
  FiActivity,
  FiTrendingUp,
  FiAward,
  FiPackage,
  FiClock,
  FiGlobe,
  FiLock
} from 'react-icons/fi';
import { getUserData } from '../utils/auth';
import { environment } from '../utils/environment';


const PageContainer = styled.div`
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a0a2e 25%, #0a0a0a 50%, #16213e 75%, #0a0a0a 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  padding: 2rem;
  
  @keyframes gradientShift {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
`;

const ContentWrapper = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

const Header = styled.div`
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
`;

const Title = styled.h1`
  background: linear-gradient(135deg, #FF6B9D 0%, #C471ED 25%, #9F7AEA 50%, #12C2E9 75%, #F64F59 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-size: 200% 200%;
  animation: gradientFlow 3s ease infinite;
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  letter-spacing: 1px;
  text-transform: uppercase;
  filter: drop-shadow(0 0 20px rgba(159, 122, 234, 0.5));
  
  @keyframes gradientFlow {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
`;

const Subtitle = styled.p`
  background: linear-gradient(90deg, #B794F4 0%, #9F7AEA 50%, #FF6B9D 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 1.1rem;
  font-weight: 300;
  letter-spacing: 0.5px;
`;

const Card = styled.div`
  background: linear-gradient(145deg, #1a1a1a 0%, #151515 100%);
  border: 2px solid transparent;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 8px 24px rgba(128, 90, 213, 0.25), 0 4px 12px rgba(0, 0, 0, 0.6);
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  background-clip: padding-box;
  
  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: -1;
    margin: -2px;
    border-radius: inherit;
    background: linear-gradient(135deg, #FF6B9D 0%, #C471ED 20%, #9F7AEA 40%, #12C2E9 60%, #805AD5 80%, #F64F59 100%);
  }
  
  &:after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
    background: linear-gradient(90deg, #FF6B9D 0%, #C471ED 25%, #9F7AEA 50%, #12C2E9 75%, #F64F59 100%);
    background-size: 200% 200%;
    animation: borderFlow 3s linear infinite;
    border-radius: 16px 16px 0 0;
    
    @keyframes borderFlow {
      0% { background-position: 0% 50%; }
      100% { background-position: 200% 50%; }
    }
  }
`;

const CardTitle = styled.h2`
  background: linear-gradient(90deg, #FF6B9D 0%, #C471ED 33%, #9F7AEA 66%, #12C2E9 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 3px solid transparent;
  border-image: linear-gradient(90deg, #FF6B9D 0%, #C471ED 33%, #9F7AEA 66%, #12C2E9 100%);
  border-image-slice: 1;
  text-transform: uppercase;
  letter-spacing: 1px;
`;

const InfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #2a2a2a;

  &:last-child {
    border-bottom: none;
  }
`;

const InfoLabel = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #b0b0b0;
  font-weight: 600;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  
  svg {
    font-size: 1.25rem;
    filter: drop-shadow(0 0 8px currentColor);
    
    &:nth-child(1) {
      color: #FF6B9D;
    }
  }
`;

const InfoValue = styled.span`
  color: #e0e0e0;
  font-weight: 600;
  font-size: 1rem;
`;

const Badge = styled.span`
  display: inline-block;
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #FF6B9D 0%, #C471ED 50%, #9F7AEA 100%);
  color: #ffffff;
  border: 2px solid transparent;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(255, 107, 157, 0.4), 0 0 20px rgba(159, 122, 234, 0.3);
  position: relative;
  background-clip: padding-box;
  
  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: -1;
    margin: -2px;
    border-radius: inherit;
    background: linear-gradient(135deg, #F64F59 0%, #FF6B9D 25%, #C471ED 50%, #9F7AEA 75%, #12C2E9 100%);
  }
`;

const TrainerCard = styled(Card)`
  background: linear-gradient(145deg, #1f1f1f 0%, #1a1a1a 100%);
  
  &:before {
    background: linear-gradient(135deg, #12C2E9 0%, #9F7AEA 20%, #C471ED 40%, #FF6B9D 60%, #F64F59 80%, #12C2E9 100%);
    animation: rainbowRotate 6s linear infinite;
    
    @keyframes rainbowRotate {
      0% { filter: hue-rotate(0deg); }
      100% { filter: hue-rotate(360deg); }
    }
  }
  
  &:after {
    background: linear-gradient(90deg, #12C2E9 0%, #9F7AEA 20%, #C471ED 40%, #FF6B9D 60%, #F64F59 80%, #12C2E9 100%);
    background-size: 200% 200%;
    animation: borderFlow 3s linear infinite;
  }
`;

const StatBadge = styled.div`
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 1rem;
  background: linear-gradient(135deg, #C471ED 0%, #9F7AEA 100%);
  color: #ffffff;
  border: 2px solid transparent;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.3px;
  position: relative;
  background-clip: padding-box;
  box-shadow: 0 4px 12px rgba(196, 113, 237, 0.4);
  
  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: -1;
    margin: -2px;
    border-radius: inherit;
    background: linear-gradient(135deg, #FF6B9D 0%, #C471ED 33%, #9F7AEA 66%, #12C2E9 100%);
  }
  
  &:nth-of-type(1) {
    background: linear-gradient(135deg, #FF6B9D 0%, #F64F59 100%);
    box-shadow: 0 4px 12px rgba(255, 107, 157, 0.4);
  }
  
  &:nth-of-type(2) {
    background: linear-gradient(135deg, #12C2E9 0%, #0BA5C2 100%);
    box-shadow: 0 4px 12px rgba(18, 194, 233, 0.4);
  }
  
  &:nth-of-type(3) {
    background: linear-gradient(135deg, #C471ED 0%, #9F7AEA 100%);
    box-shadow: 0 4px 12px rgba(159, 122, 234, 0.4);
  }
  
  svg {
    font-size: 1rem;
    filter: drop-shadow(0 0 4px rgba(255, 255, 255, 0.5));
  }
`;

const QuickActionButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #FF6B9D 0%, #C471ED 100%);
  color: #ffffff;
  border: 2px solid transparent;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
  
  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.5s;
  }
  
  &:nth-of-type(1) {
    background: linear-gradient(135deg, #FF6B9D 0%, #F64F59 100%);
    box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
  }
  
  &:nth-of-type(2) {
    background: linear-gradient(135deg, #12C2E9 0%, #0BA5C2 100%);
    box-shadow: 0 4px 15px rgba(18, 194, 233, 0.4);
  }
  
  &:nth-of-type(3) {
    background: linear-gradient(135deg, #C471ED 0%, #9F7AEA 100%);
    box-shadow: 0 4px 15px rgba(159, 122, 234, 0.4);
  }
  
  svg {
    font-size: 1.1rem;
    filter: drop-shadow(0 0 4px rgba(255, 255, 255, 0.5));
  }
  
  &:active {
    transform: translateY(0) scale(1);
  }
`;

const QuickActionsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
`;

const ProfilePage: React.FC = () => {
  console.log('[ProfilePage] Rendering Profile Page!');
  
  const [userStats, setUserStats] = useState({
    totalTrades: 0,
    activeTrades: 0,
    completedTrades: 0,
    activeListings: 0,
    totalListings: 0,
    rating: 'New Trainer'
  });
  const [loading, setLoading] = useState(true);
  const [userFullData, setUserFullData] = useState<any>(null);
  
  const userData = getUserData();
  const userId = userData?.userId || null;
  const username = userData?.username || 'Guest Trainer';
  const email = userData?.email || 'trainer@pokemarketplace.com';
  
  useEffect(() => {
    const fetchUserStats = async () => {
      if (!userId) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const apiGateway = environment.apiUrl || 'http://localhost:8080';

        
        // Fetch user details
        const userResponse = await fetch(`${apiGateway}/api/users/${userId}`);
        if (userResponse.ok) {
          const user = await userResponse.json();
          setUserFullData(user);
        }

        // Fetch user's trades
        const tradesResponse = await fetch(`${apiGateway}/api/trades/user/${userId}`);
        const trades = tradesResponse.ok ? await tradesResponse.json() : [];
        
        // Fetch user's listings
        const listingsResponse = await fetch(`${apiGateway}/api/listings/user/${userId}`);
        const listings = listingsResponse.ok ? await listingsResponse.json() : [];
        
        // Calculate statistics
        const completedTrades = trades.filter((t: any) => t.tradeStatus === 'ACCEPTED').length;
        const pendingTrades = trades.filter((t: any) => t.tradeStatus === 'PENDING').length;
        const activeListings = listings.filter((l: any) => l.listingStatus === 'ACTIVE').length;
        
        // Calculate rating based on completed trades
        let rating = 'New Trainer';
        if (completedTrades >= 50) rating = 'Master Trainer';
        else if (completedTrades >= 20) rating = 'Expert Trainer';
        else if (completedTrades >= 10) rating = 'Advanced Trainer';
        else if (completedTrades >= 5) rating = 'Intermediate Trainer';
        else if (completedTrades >= 1) rating = 'Beginner Trainer';
        
        setUserStats({
          totalTrades: trades.length,
          activeTrades: pendingTrades,
          completedTrades: completedTrades,
          totalListings: listings.length,
          activeListings: activeListings,
          rating: rating
        });
        
        console.log('[ProfilePage] Stats fetched:', { trades: trades.length, listings: listings.length });
      } catch (error) {
        console.error('[ProfilePage] Error fetching user stats:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchUserStats();
  }, [userId]);
  
  const handleViewListings = () => {
    window.location.href = '/listings';
  };
  
  const handleViewTrades = () => {
    window.location.href = '/trades';
  };
  
  const handleBrowseCards = () => {
    window.location.href = '/cards';
  };
  
  return (
    <PageContainer>
      <ContentWrapper>
        <Header>
          <Title>{username}'s Profile</Title>
        </Header>

        <TrainerCard>
          <CardTitle>User Information</CardTitle>
          <InfoRow>
            <InfoLabel>
              <FiUser />
              <span>Username</span>
            </InfoLabel>
            <InfoValue>{username}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiMail />
              <span>Contact</span>
            </InfoLabel>
            <InfoValue>{email}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiCalendar />
              <span>Trainer Since</span>
            </InfoLabel>
            <InfoValue>
              {userFullData?.createdAt 
                ? new Date(userFullData.createdAt).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
                : new Date().toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
              }
            </InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiShield />
              <span>Trainer Status</span>
            </InfoLabel>
            <Badge>Active Trainer</Badge>
          </InfoRow>
        </TrainerCard>

        <Card>
          <CardTitle>Trading Statistics</CardTitle>
          <InfoRow>
            <InfoLabel>
              <FiActivity />
              <span>Total Trades</span>
            </InfoLabel>
            <StatBadge>
              <FiTrendingUp />
              <span>{userStats.totalTrades} Completed</span>
            </StatBadge>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiPackage />
              <span>Active Trades</span>
            </InfoLabel>
            <StatBadge>
              <FiClock />
              <span>{userStats.activeTrades} Pending</span>
            </StatBadge>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiPackage />
              <span>Active Listings</span>
            </InfoLabel>
            <StatBadge>
              <FiTrendingUp />
              <span>{userStats.activeListings} Live</span>
            </StatBadge>
          </InfoRow>
          <InfoRow>
            <InfoLabel>
              <FiAward />
              <span>Marketplace Rating</span>
            </InfoLabel>
            <StatBadge>
              <FiAward />
              <span>{userStats.rating}</span>
            </StatBadge>
          </InfoRow>
        </Card>

        <Card>
          <CardTitle>Quick Actions</CardTitle>
          <QuickActionsGrid>
            <QuickActionButton onClick={handleViewListings}>
              <FiPackage />
              <span>My Listings</span>
            </QuickActionButton>
            <QuickActionButton onClick={handleViewTrades}>
              <FiActivity />
              <span>My Trades</span>
            </QuickActionButton>
            <QuickActionButton onClick={handleBrowseCards}>
              <FiTrendingUp />
              <span>Browse Cards</span>
            </QuickActionButton>
          </QuickActionsGrid>
        </Card>

        {loading && (
          <Card style={{ textAlign: 'center', padding: '3rem' }}>
            <p style={{ color: '#B794F4', fontSize: '1.2rem' }}>Loading your profile data...</p>
          </Card>
        )}
      </ContentWrapper>
    </PageContainer>
  );
};

export default ProfilePage;
