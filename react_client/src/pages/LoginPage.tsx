import Avatar from "@mui/material/Avatar";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Link from "@mui/material/Link";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import { FC, useState } from "react";
import { useNavigate } from "react-router";
import { Copyright } from "../components/features/login/Copyright";
import { useForm } from "react-hook-form";
import { LoginRequestValidator } from "../core/types/login/LoginRequest.validator";
import { yupResolver } from "@hookform/resolvers/yup";
import { LoginRequest } from "../core/types/login/LoginRequest.types";
import {
  FormControl,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { LoginModal } from "../components/features/login/LoginModal.component";
import axios, { AxiosError } from "axios";
import { axiosApi } from "../tools/configuration/axios-config";
import AppSnackbar from "../components/AppSnackbar";
import { LoadingButton } from "@mui/lab";

export const LoginPage: FC = () => {
  const navigate = useNavigate();
  const [resetPasswordModal, setResetPasswordModal] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const {
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(LoginRequestValidator),
  });

  function sendLoginRequest(userLoginRequest: LoginRequest) {
    setLoading(true);
    const dataToSend = new FormData();
    dataToSend.append("email", userLoginRequest.email);
    dataToSend.append("password", userLoginRequest.password);
    dataToSend.append("token", userLoginRequest.token);

    axiosApi
      .post("/api/auth/login", dataToSend, { withCredentials: true })
      .then((result) => {
        if (result.status === 200) {
          navigate("/");
        }
      })
      .catch((error) => {
        if (axios.isAxiosError(error)) {
          const axiosError: AxiosError = error as AxiosError;
          if (axiosError.response && axiosError.response.status === 400) {
            setError(JSON.stringify(axiosError.response.data));
          } else {
            setError("Error occured when trying to login");
          }
        } else {
          setError("Error occured when trying to login");
        }
      })
      .finally(() => setLoading(false));
  }

  return (
    <Grid container component="main" sx={{ height: "100vh" }}>
      <AppSnackbar
        open={!!error}
        message={error}
        onClose={() => setError(null)}
      />
      <LoginModal
        isOpen={resetPasswordModal}
        handleClose={() => setResetPasswordModal(false)}
      />
      <CssBaseline />
      <Grid
        item
        xs={false}
        sm={4}
        md={7}
        sx={{
          backgroundImage: "url(https://source.unsplash.com/random?wallpapers)",
          backgroundRepeat: "no-repeat",
          backgroundColor: (t) =>
            t.palette.mode === "light"
              ? t.palette.grey[50]
              : t.palette.grey[900],
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      />
      <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square>
        <Box
          sx={{
            my: 8,
            mx: 4,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Sign in
          </Typography>
          <Box component="form" noValidate sx={{ mt: 1 }}>
            <TextField
              onChange={(event) => setValue("email", event.target.value)}
              error={!!errors.email}
              helperText={!!errors.email ? errors.email.message : ""}
              margin="normal"
              required
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
            />
            <FormControl variant="outlined" fullWidth required>
              <InputLabel htmlFor="outlined-adornment-password">
                Password
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-password"
                onChange={(event) => setValue("password", event.target.value)}
                error={!!errors.password}
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
            <TextField
              onChange={(event) => setValue("token", event.target.value)}
              error={!!errors.token}
              helperText={!!errors.token ? errors.token.message : ""}
              margin="normal"
              required
              fullWidth
              label="Token"
              name="token"
              autoFocus
            />
            <LoadingButton
              type="submit"
              loading={loading}
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              onClick={handleSubmit(sendLoginRequest)}
            >
              {!loading ? "Sign In" : "Loading"}
            </LoadingButton>
            <Grid container>
              <Grid item xs>
                <Link
                  onClick={() => setResetPasswordModal(true)}
                  variant="body2"
                  className={"cursor-pointer"}
                >
                  Forgot password?
                </Link>
              </Grid>
              <Grid item>
                <Link
                  variant="body2"
                  onClick={() => navigate("/register")}
                  className="cursor-pointer"
                >
                  {"Don't have an account? Sign Up"}
                </Link>
              </Grid>
            </Grid>
            <Copyright />
          </Box>
        </Box>
      </Grid>
    </Grid>
  );
};
