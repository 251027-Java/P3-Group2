/**
 * This file was created by Claude Sonnet 4.5
 */
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  rootDir: '.',
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    '^@marketplace/shared-utils$': '<rootDir>/../shared-utils/src/index.ts',
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  testMatch: [
    '<rootDir>/src/**/__tests__/**/*.{js,jsx,ts,tsx}',
    '<rootDir>/src/**/*.{spec,test}.{js,jsx,ts,tsx}',
  ],
  collectCoverageFrom: [
    'src/**/*.{js,jsx,ts,tsx}',
    '!src/**/*.d.ts',
    '!src/index.tsx',
    '!src/**/*.stories.{js,jsx,ts,tsx}',
  ],
  coverageThreshold: {
    global: {
      branches: 60,
      functions: 60,
      lines: 60,
      statements: 60,
    },
  },
  transform: {
    '^.+\\.(ts|tsx)$': 'ts-jest',
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
};
