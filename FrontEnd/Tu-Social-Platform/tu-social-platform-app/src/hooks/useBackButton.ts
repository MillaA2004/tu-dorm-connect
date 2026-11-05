import { useNavigate } from "react-router-dom";

export const useBackButton = (fallbackPath = "/") => {
  const navigate = useNavigate();

  const goBack = () => {
    if (window.history.state && window.history.state.idx > 0) {
      navigate(-1);
    } else {
      navigate(fallbackPath, { replace: true });
    }
  };

  return goBack;
};
