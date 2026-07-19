import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

const TOKEN_STORAGE_KEY = "family-planner-token";

interface AuthState {
  token: string | null;
  sessionExpired: boolean;
  permissionError: string | null;
}

function readStoredToken(): string | null {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

const initialState: AuthState = {
  token: readStoredToken(),
  sessionExpired: false,
  permissionError: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    tokenStored: (state, action: PayloadAction<string>) => {
      state.token = action.payload;
      state.sessionExpired = false;
      state.permissionError = null;
      localStorage.setItem(TOKEN_STORAGE_KEY, action.payload);
    },
    tokenCleared: (state) => {
      state.token = null;
      state.sessionExpired = false;
      state.permissionError = null;
      localStorage.removeItem(TOKEN_STORAGE_KEY);
    },
    sessionExpired: (state) => {
      state.token = null;
      state.sessionExpired = true;
      state.permissionError = null;
      localStorage.removeItem(TOKEN_STORAGE_KEY);
    },
    permissionDenied: (state) => {
      state.permissionError =
        "Sie haben keine Berechtigung für diesen Bereich.";
    },
    permissionErrorCleared: (state) => {
      state.permissionError = null;
    },
  },
});

export const {
  tokenStored,
  tokenCleared,
  sessionExpired,
  permissionDenied,
  permissionErrorCleared,
} = authSlice.actions;
export default authSlice.reducer;
