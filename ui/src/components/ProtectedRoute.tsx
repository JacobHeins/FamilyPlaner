import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAppSelector } from "../app/store/hooks";

export default function ProtectedRoute() {
  const { token, sessionExpired } = useAppSelector((state) => state.auth);
  const location = useLocation();

  if (!token) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location, sessionExpired }}
      />
    );
  }

  return <Outlet />;
}
