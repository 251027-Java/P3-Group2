/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const PageContainer = styled.div`
  min-height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
`;

const Title = styled.h1`
  color: #333;
  margin-bottom: 2rem;
`;

const Subtitle = styled.p`
  color: #666;
  margin-bottom: 1rem;
`;

const StyledLink = styled(Link)`
  color: #667eea;
  font-weight: 500;

  &:hover {
    color: #764ba2;
    text-decoration: underline;
  }
`;

const LoginPage: React.FC = () => {
  return (
    <PageContainer>
      <Title>Login Page</Title>
      <Subtitle>Login form will be implemented here with Aurora background</Subtitle>
      <div>
        Don&apos;t have an account? <StyledLink to="/users/auth/register">Register here</StyledLink>
      </div>
    </PageContainer>
  );
};

export default LoginPage;
