import React from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { FiTrendingUp, FiUsers, FiPackage } from 'react-icons/fi';
import AuroraBackground from '../components/AuroraBackground';
import logo from '../assets/logo.png';

const PageWrapper = styled.div`
  position: relative;
  min-height: 100vh;
`;

const HomeContainer = styled.div`
  min-height: 100vh;
  padding: 4rem 2rem;
  position: relative;
  z-index: 1;
`;

const Hero = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 4rem 0;
  position: relative;
  z-index: 1;
`;

const Logo = styled.img`
  width: 600px;
  height: auto;
  margin-bottom: 2rem;
  filter: drop-shadow(0 0 30px rgba(159, 122, 234, 0.6));
`;

const Title = styled.h1`
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  background: linear-gradient(135deg, #FFFFFF 0%, #D6BCFA 50%, #9F7AEA 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 40px rgba(159, 122, 234, 0.4);
  
  @media (max-width: 768px) {
    font-size: 2rem;
  }
`;

const Subtitle = styled.p`
  font-size: 1.5rem;
  color: #ffffff;
  margin-bottom: 3rem;
  line-height: 1.6;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  
  @media (max-width: 768px) {
    font-size: 1.2rem;
  }
`;

const CTAContainer = styled.div`
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
`;

const CTAButton = styled.a`
  padding: 1rem 2.5rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1.1rem;
  text-decoration: none;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  
  &.primary {
    background: linear-gradient(135deg, #9F7AEA 0%, #6B46C1 100%);
    color: white;
    border: 1px solid rgba(159, 122, 234, 0.3);
    box-shadow: 0 4px 15px rgba(159, 122, 234, 0.3);
    
    &:hover {
      background: linear-gradient(135deg, #C471ED 0%, #9F7AEA 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(159, 122, 234, 0.5);
    }
  }
  
  &.secondary {
    background: transparent;
    color: #FFFFFF;
    border: 2px solid #FFFFFF;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      transform: translateY(-2px);
    }
  }
`;

const FeaturesGrid = styled.div`
  max-width: 1200px;
  margin: 6rem auto 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  position: relative;
  z-index: 1;
`;

const FeatureCard = styled.div`
  background: rgba(31, 31, 31, 0.8);
  backdrop-filter: blur(10px);
  padding: 2rem;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid rgba(159, 122, 234, 0.3);
  
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
    background: linear-gradient(135deg, #9F7AEA 0%, #C471ED 50%, #FF6B9D 100%);
    opacity: 0.3;
  }
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 30px rgba(159, 122, 234, 0.4);
    
    &:before {
      opacity: 0.5;
    }
  }
  
  svg {
    font-size: 2.5rem;
    color: #D6BCFA;
    margin-bottom: 1rem;
    filter: drop-shadow(0 0 10px rgba(159, 122, 234, 0.5));
  }
  
  h3 {
    color: #ffffff;
    font-size: 1.5rem;
    margin-bottom: 0.5rem;
  }
  
  p {
    color: #e0e0e0;
    line-height: 1.6;
  }
`;

const HomePage: React.FC = () => {
  return (
    <PageWrapper>
      <AuroraBackground />
      <HomeContainer>
        <Hero>
          <Logo src={logo} alt="Marketplace Logo" />
          <Title>Welcome. Start Trading Today.</Title>
          <Subtitle>
            Your ultimate platform for trading, connecting with trainers, and discovering amazing listings.
          </Subtitle>
          <CTAContainer>
            <CTAButton href="/signup" className="primary">
              Get Started
            </CTAButton>
          </CTAContainer>
        </Hero>
        
        <FeaturesGrid>
          <FeatureCard>
            <FiTrendingUp />
            <h3>Track Your Trades</h3>
            <p>Monitor all your trading activities in real-time with comprehensive analytics and insights.</p>
          </FeatureCard>
          
          <FeatureCard>
            <FiUsers />
            <h3>Connect with Trainers</h3>
            <p>Build your network with experienced trainers and grow your trading community.</p>
          </FeatureCard>
          
          <FeatureCard>
            <FiPackage />
            <h3>Browse Listings</h3>
            <p>Discover curated listings with detailed information and competitive pricing.</p>
          </FeatureCard>
        </FeaturesGrid>
      </HomeContainer>
    </PageWrapper>
  );
};

export default HomePage;
