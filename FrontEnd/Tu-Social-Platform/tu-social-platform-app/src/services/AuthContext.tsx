import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import type { AuthUser, RegisterFormValues } from "./authTypes";
import {
  loginUser,
  logoutUser,
  registerUser,
  restoreSessionFromStoredToken,
} from "./AuthService";

interface AuthState {
  user: AuthUser | null;
  token: string | null;
  loading: boolean;
}

interface AuthContextValue extends AuthState {
  register: (form: RegisterFormValues) => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: null,
    loading: true,
  });

  
  useEffect(() => {
    let cancelled = false;

    (async () => {
      const session = await restoreSessionFromStoredToken();
      if (cancelled) return;

      if (session) {
        setState({
          user: session.user,
          token: session.token,
          loading: false,
        });
      } else {
        setState((prev) => ({ ...prev, loading: false }));
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  const register = async (form: RegisterFormValues) => {
    const { token, user } = await registerUser(form);
    setState({
      user,
      token,
      loading: false,
    });
  };

  const login = async (email: string, password: string) => {
    const { token, user } = await loginUser({ email, password });
    setState({
      user,
      token,
      loading: false,
    });
  };

  const logout = () => {
    logoutUser();
    setState({
      user: null,
      token: null,
      loading: false,
    });
  };

  const value: AuthContextValue = {
    ...state,
    register,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
};
