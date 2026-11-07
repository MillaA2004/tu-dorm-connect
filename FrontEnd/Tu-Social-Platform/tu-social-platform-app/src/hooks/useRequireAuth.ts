import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthContext";

export const useRequireAuth = (redirectUrl = "/login") => {
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // If there is no user, redirect them immediately
    if (!user) {
      navigate(redirectUrl, { replace: true });
    }
  }, [user, navigate, redirectUrl]);

  // Return the user so the component can still use their data (like user.id)
  return user;
};
