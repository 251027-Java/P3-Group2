/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const PageContainer = styled.div`
  min-height: calc(100vh - 80px);
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
`;

const Title = styled.h1`
  color: #333;
  margin-bottom: 2rem;
`;

const Content = styled.div`
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

const StyledLink = styled(Link)`
  color: #667eea;
  font-weight: 500;
  display: inline-block;
  margin-top: 1rem;

  &:hover {
    color: #764ba2;
    text-decoration: underline;
  }
`;

const SettingsPage: React.FC = () => {
  return (
    <PageContainer>
      <Title>User Settings</Title>
      <Content>
        <p>User settings and preferences will be displayed here</p>
        <StyledLink to="/users/profile">Back to Profile</StyledLink>
      </Content>
    </PageContainer>
  );
};

export default SettingsPage;
