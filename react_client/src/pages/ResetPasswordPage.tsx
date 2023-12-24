import { FC, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router";
import {
  FormControl,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { LoadingButton } from "@mui/lab";
import { useForm } from "react-hook-form";
import * as yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { axiosApi } from "../tools/configuration/axios-config";
import AppSnackbar from "../components/AppSnackbar";
import axios, { AxiosError } from "axios";

type BaseResetPasswordRequest = {
  password: string;
  confirmPassword: string;
};

const resetPasswordValidator = yup.object().shape({
  password: yup.string().required("Password is required"),
  confirmPassword: yup
    .string()
    .required("Confirmation password is required")
    .oneOf([yup.ref("password")], "Confirm password must match password"),
});

export const ResetPasswordPage: FC = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const randomKey = queryParams.get("randomKey");
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  const {
    formState: { errors },
    handleSubmit,
    setValue,
    reset,
  } = useForm({
    resolver: yupResolver(resetPasswordValidator),
  });

  useEffect(() => {
    if (!randomKey) {
      navigate("/login");
    }
  }, [navigate, randomKey]);

  function sendResetPasswordRequest(
    resetPasswordRequest: BaseResetPasswordRequest
  ) {
    setLoading(true);

    axiosApi
      .post("/api/auth/confirm-reset-password", {
        ...resetPasswordRequest,
        resetKey: randomKey,
      })
      .then((result) => {
        if (result.status === 200) {
          setSuccess(true);
        }
      })
      .catch((error) => {
        if (axios.isAxiosError(error)) {
          const axiosError: AxiosError = error as AxiosError;
          if (axiosError.response && axiosError.response.status === 400) {
            setError(JSON.stringify(axiosError.response.data));
          } else {
            setError("Error occurred when trying to login");
          }
        } else {
          setError("Error occurred when trying to login");
        }
      })
      .finally(() => setLoading(false));
  }

  return (
    <div
      className={
        "flex justify-center items-center bg-gray-800 flex-col h-screen"
      }
    >
      <div className={"w-1/2 flex flex-col gap-2"}>
        <AppSnackbar
          open={!!error}
          message={error}
          onClose={() => setError(null)}
        />
        <AppSnackbar
          open={success}
          message={
            "Successfully changed the password - now try to login with this password!"
          }
          severity={"success"}
          duration={3000}
          onClose={() => {
            setSuccess(false);
            setError(null);
            reset();
            navigate("/login");
          }}
        />
        <p className={"font-bold text-4xl font-sans text-white text-center"}>
          Reset password
        </p>
        <p className={"text-white font-bold text-base"}>
          Please enter new credentials to reset your current password
        </p>
        <FormControl variant="outlined" fullWidth required>
          <InputLabel htmlFor="password">Password</InputLabel>
          <OutlinedInput
            id="password"
            onChange={(event) => setValue("password", event.target.value)}
            error={!!errors.password}
            inputProps={{ style: { color: "white" } }}
            color={"secondary"}
            type={showPassword ? "text" : "password"}
            fullWidth
            required
            label="Password"
            name="password"
            endAdornment={
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={() => setShowPassword(!showPassword)}
                  edge="end"
                >
                  {!showPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            }
          />
          {!!errors.password && (
            <FormHelperText error>{errors.password.message}</FormHelperText>
          )}
        </FormControl>
        <FormControl variant="outlined" fullWidth required>
          <InputLabel htmlFor="confirm-password">Confirm password</InputLabel>
          <OutlinedInput
            id="confirm-password"
            inputProps={{ style: { color: "white" } }}
            color={"secondary"}
            onChange={(event) =>
              setValue("confirmPassword", event.target.value)
            }
            error={!!errors.confirmPassword}
            type={showPassword ? "text" : "password"}
            fullWidth
            required
            label="Confirm password"
            name="confirmPassword"
            endAdornment={
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={() => setShowPassword(!showPassword)}
                  edge="end"
                >
                  {!showPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            }
          />
          {!!errors.confirmPassword && (
            <FormHelperText error>
              {errors.confirmPassword.message}
            </FormHelperText>
          )}
        </FormControl>
        <LoadingButton
          type="submit"
          loading={loading}
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
          onClick={handleSubmit(sendResetPasswordRequest)}
        >
          {!loading ? "Reset password" : "Loading"}
        </LoadingButton>
      </div>
    </div>
  );
};
