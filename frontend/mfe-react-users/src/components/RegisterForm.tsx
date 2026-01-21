/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * RegisterFormComponent
 * Handles user registration with multi-step validation
 */
import React, { useState } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

const FormContainer = styled.div`
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
`;

const FormWrapper = styled.div`
  background: white;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 450px;
  width: 100%;
`;

const FormTitle = styled.h2`
  color: #333;
  margin-bottom: 0.5rem;
  text-align: center;
  font-size: 1.8rem;
`;

const FormSubtitle = styled.p`
  color: #999;
  text-align: center;
  margin-bottom: 2rem;
  font-size: 0.95rem;
`;

const StepIndicator = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
  gap: 1rem;
`;

const StepDot = styled.div<{ $active?: boolean; $completed?: boolean }>`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: 600;
  background-color: ${(props) =>
    props.$completed ? '#667eea' : props.$active ? '#667eea' : '#e0e0e0'};
  color: ${(props) => (props.$completed || props.$active ? 'white' : '#999')};
  transition: all 0.3s;
`;

const FormGroup = styled.div`
  margin-bottom: 1.5rem;
`;

const Label = styled.label`
  display: block;
  color: #333;
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
`;

const Input = styled.input`
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;

  &:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  &:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
  }
`;

const PasswordStrength = styled.div`
  margin-top: 0.5rem;
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 2px;
  overflow: hidden;
`;

const PasswordStrengthBar = styled.div<{ $strength: number }>`
  height: 100%;
  width: ${(props) => props.$strength}%;
  background-color: ${(props) => {
    if (props.$strength < 30) return '#f44336';
    if (props.$strength < 60) return '#ff9800';
    if (props.$strength < 80) return '#ffc107';
    return '#4caf50';
  }};
  transition: all 0.3s;
`;

const PasswordStrengthText = styled.p`
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #666;
`;

const CheckboxGroup = styled.div`
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin-bottom: 1rem;
`;

const Checkbox = styled.input`
  cursor: pointer;
  width: 18px;
  height: 18px;
  margin-top: 2px;
`;

const CheckboxLabel = styled.label`
  color: #666;
  font-size: 0.9rem;
  cursor: pointer;
  line-height: 1.4;

  a {
    color: #667eea;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
`;

const ErrorMessage = styled.div`
  background-color: #fee;
  color: #c33;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
`;

const SuccessMessage = styled.div`
  background-color: #efe;
  color: #3c3;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
`;

const Button = styled.button<{ $primary?: boolean }>`
  flex: 1;
  padding: 0.875rem 1rem;
  background: ${(props) =>
    props.$primary ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' : '#e0e0e0'};
  color: ${(props) => (props.$primary ? 'white' : '#333')};
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;

  &:hover:not(:disabled) {
    transform: ${(props) => (props.$primary ? 'translateY(-2px)' : 'none')};
    box-shadow: ${(props) =>
      props.$primary ? '0 10px 20px rgba(102, 126, 234, 0.3)' : 'none'};
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
`;

const LinkContainer = styled.div`
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #666;

  a {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;

    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }
  }
`;

interface RegisterFormProps {
  onSuccess?: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess }) => {
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [acceptTerms, setAcceptTerms] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const calculatePasswordStrength = (pwd: string): number => {
    let strength = 0;
    if (pwd.length >= 8) strength += 20;
    if (pwd.length >= 12) strength += 20;
    if (/[a-z]/.test(pwd)) strength += 15;
    if (/[A-Z]/.test(pwd)) strength += 15;
    if (/[0-9]/.test(pwd)) strength += 15;
    if (/[^a-zA-Z0-9]/.test(pwd)) strength += 15;
    return Math.min(strength, 100);
  };

  const passwordStrength = calculatePasswordStrength(password);

  const validateStep1 = (): boolean => {
    if (!email.trim()) {
      setError('Email is required');
      return false;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('Please enter a valid email');
      return false;
    }
    if (!username.trim()) {
      setError('Username is required');
      return false;
    }
    if (username.length < 3) {
      setError('Username must be at least 3 characters');
      return false;
    }
    setError('');
    return true;
  };

  const validateStep2 = (): boolean => {
    if (!password) {
      setError('Password is required');
      return false;
    }
    if (password.length < 8) {
      setError('Password must be at least 8 characters');
      return false;
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return false;
    }
    if (!acceptTerms) {
      setError('You must accept the terms and conditions');
      return false;
    }
    setError('');
    return true;
  };

  const handleNext = () => {
    if (validateStep1()) {
      setStep(2);
    }
  };

  const handleBack = () => {
    setStep(1);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateStep2()) {
      return;
    }

    setLoading(true);
    try {
      await AuthService.register({
        email,
        username,
        password,
      });

      if (onSuccess) {
        onSuccess();
      } else {
        navigate('/users/profile');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormContainer>
      <FormWrapper>
        <FormTitle>Create Account</FormTitle>
        <FormSubtitle>Join our marketplace</FormSubtitle>

        <StepIndicator>
          <StepDot $completed={step > 1} $active={step === 1}>
            1
          </StepDot>
          <StepDot $active={step === 2}>2</StepDot>
        </StepIndicator>

        {error && <ErrorMessage>{error}</ErrorMessage>}

        {step === 1 ? (
          <form>
            <FormGroup>
              <Label htmlFor="email">Email Address</Label>
              <Input
                id="email"
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                type="text"
                placeholder="Choose a username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
              />
            </FormGroup>

            <ButtonGroup>
              <Button as="a" href="/users/auth/login">
                Back to Login
              </Button>
              <Button $primary onClick={handleNext} disabled={loading}>
                Next
              </Button>
            </ButtonGroup>
          </form>
        ) : (
          <form onSubmit={handleSubmit}>
            <FormGroup>
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="Create a strong password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
              {password && (
                <>
                  <PasswordStrength>
                    <PasswordStrengthBar $strength={passwordStrength} />
                  </PasswordStrength>
                  <PasswordStrengthText>
                    {passwordStrength < 30
                      ? 'Weak'
                      : passwordStrength < 60
                        ? 'Fair'
                        : passwordStrength < 80
                          ? 'Good'
                          : 'Strong'}
                  </PasswordStrengthText>
                </>
              )}
            </FormGroup>

            <FormGroup>
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                disabled={loading}
              />
            </FormGroup>

            <CheckboxGroup>
              <Checkbox
                id="acceptTerms"
                type="checkbox"
                checked={acceptTerms}
                onChange={(e) => setAcceptTerms(e.target.checked)}
                disabled={loading}
              />
              <CheckboxLabel htmlFor="acceptTerms">
                I agree to the <a href="#">Terms and Conditions</a> and{' '}
                <a href="#">Privacy Policy</a>
              </CheckboxLabel>
            </CheckboxGroup>

            <ButtonGroup>
              <Button onClick={handleBack} disabled={loading}>
                Back
              </Button>
              <Button $primary type="submit" disabled={loading}>
                {loading ? 'Creating Account...' : 'Create Account'}
              </Button>
            </ButtonGroup>
          </form>
        )}

        <LinkContainer>
          Already have an account? <a href="/users/auth/login">Login here</a>
        </LinkContainer>
      </FormWrapper>
    </FormContainer>
  );
};

export default RegisterForm;
