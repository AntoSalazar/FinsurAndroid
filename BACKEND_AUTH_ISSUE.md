# Backend Authentication Issue

## Problem Summary

The backend authentication is inconsistent and not properly recognizing session cookies for authenticated endpoints. This causes the following issues:

1. **Cart endpoint creates new sessions**: When fetching the current cart, the backend creates a new unauthenticated session (user_id: null) instead of using the existing authenticated session
2. **Profile/Address endpoints return 401**: Endpoints like `/api/v1/addresses` return `401 Unauthorized` with error message "No token provided"

## Root Cause

The backend authentication middleware (`/app/src/api/middlewares/authentication.ts:37`) is checking for an **Authorization token** in the request headers, but the authentication flow uses **cookie-based session authentication**:

- Login/Register endpoints (`/api/v1/auth/login`, `/api/v1/auth/register`) create a session and set a cookie (`hardware.sid`)
- The cookie is being sent correctly with all subsequent requests
- However, the authentication middleware is ONLY checking for a token, not checking the session

## Evidence from Logs

```
2025-11-27 16:03:35: POST /api/v1/carts/items → 201 (cart_id: 11, user_id: 3) ✓
2025-11-27 16:03:42: GET /api/v1/carts/current → 200 (cart_id: 12, user_id: null) ✗
2025-11-27 16:03:48: GET /api/v1/addresses → 401 "No token provided" ✗
```

All requests correctly send the session cookie `hardware.sid`, but:
- Adding to cart works and is associated with user_id: 3
- Fetching cart creates a NEW session with user_id: null
- Fetching addresses returns 401 error

## Required Backend Fixes

### Option 1: Make Authentication Middleware Check Session (Recommended)

Update the authentication middleware to check for BOTH tokens AND session-based authentication:

```typescript
// /app/src/api/middlewares/authentication.ts

export const authenticate = (req: Request, res: Response, next: NextFunction) => {
  // Check for token-based auth
  const token = req.headers.authorization?.split(' ')[1];

  if (token) {
    // Verify token and set req.user
    // ... existing token validation code
  }
  // IMPORTANT: Also check for session-based auth
  else if (req.session && req.session.userId) {
    // User is authenticated via session
    req.user = { id: req.session.userId };
    return next();
  }
  else {
    throw new UnauthorizedException('No token or session provided');
  }
};
```

### Option 2: Return Tokens from Login/Register

Alternatively, modify the login/register endpoints to return a JWT token in the response body, and update the Android app to store and send this token in the Authorization header.

**This is NOT recommended** because it would require:
- Changing the backend login/register responses
- Updating the Android app to handle token storage
- Managing token refresh logic
- This contradicts the current cookie-based session design

## Affected Endpoints

Based on the error "No token provided", the following endpoints are likely affected:

- `/api/v1/users/profile` - User profile endpoint
- `/api/v1/addresses/` - Address management endpoints
- Potentially other authenticated endpoints

## Cart Endpoint Issue

The cart endpoint (`/api/v1/carts/current`) has a different problem:
- It doesn't return 401, but creates a new unauthenticated session
- This suggests it might have different middleware or fallback logic
- It should use the existing authenticated session instead of creating a new one

## Recommended Solution

1. Update the authentication middleware to check for `req.session.userId` as shown in Option 1
2. Ensure all authenticated endpoints use the same authentication middleware
3. Fix the cart endpoint to use the existing session instead of creating a new one
4. Test that session cookies are properly maintained across all endpoints

## Android App Changes (Already Implemented)

The following client-side fixes have been implemented to work around this issue:

1. **AuthStateManager**: Tracks authentication state locally
2. **AuthInterceptor**: Detects 401 responses and clears auth state
3. **Improved CookieJar**: Better cookie domain handling for ngrok URLs
4. **Auth state persistence**: Saves user ID and email on login, clears on logout

These changes improve the client-side experience but **do not fix the root cause**, which is the backend authentication middleware.
