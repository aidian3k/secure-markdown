import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
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
  Container,
  FormControl,
  FormControlLabel,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
} from "@mui/material";
import * as yup from "yup";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { error } from "console";

type RegisterRequest = {
  username: string;
  email: string;
  password: string;
  confirmationPassword: string;
};

const RegisterRequestValidator = yup.object().shape({
  username: yup
    .string()
    .required("Username field is required")
    .max(255, "Maximum number of characters is 255"),
  email: yup
    .string()
    .email("Email must be correct")
    .required("Email field is required"),
  password: yup.string().required("Password is required"),
  confirmationPassword: yup
    .string()
    .required("Confirm password is required")
    .oneOf([yup.ref("password")], "Confirm password must match password"),
});

export const RegisterPage: FC = () => {
  const navigate = useNavigate();
  const [resetPasswordModal, setResetPasswordModal] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const {
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(RegisterRequestValidator),
  });

  function sendRegisterRequest(userRegisterRequest: RegisterRequest) {
    console.log(userRegisterRequest);
  }

  return (
    <Grid container component="main" sx={{ height: "100vh" }}>
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
        <Container component="main" maxWidth="xs">
          <CssBaseline />
          <Box
            sx={{
              marginTop: 8,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
              <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
              Sign up
            </Typography>
            <Box component="form" noValidate sx={{ mt: 3 }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    onChange={(event) => setValue("email", event.target.value)}
                    error={!!errors.email}
                    helperText={!!errors.email ? errors.email.message : ""}
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    onChange={(event) =>
                      setValue("username", event.target.value)
                    }
                    error={!!errors.username}
                    helperText={
                      !!errors.username ? errors.username.message : ""
                    }
                    required
                    fullWidth
                    id="username"
                    label="Username"
                    name="username"
                    autoComplete="username"
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl variant="outlined" fullWidth required>
                    <InputLabel htmlFor="outlined-adornment-password">
                      Password
                    </InputLabel>
                    <OutlinedInput
                      id="outlined-adornment-password"
                      onChange={(event) =>
                        setValue("password", event.target.value)
                      }
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
                      <FormHelperText error>
                        {errors.password.message}
                      </FormHelperText>
                    )}
                  </FormControl>
                </Grid>
                <Grid item xs={12}>
                  <FormControl variant="outlined" fullWidth required>
                    <InputLabel htmlFor="confirm-password">
                      Confirm password
                    </InputLabel>
                    <OutlinedInput
                      id="confirm-password"
                      onChange={(event) =>
                        setValue("confirmationPassword", event.target.value)
                      }
                      error={!!errors.confirmationPassword}
                      type={showPassword ? "text" : "password"}
                      fullWidth
                      required
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
                    {!!errors.confirmationPassword && (
                      <FormHelperText error>
                        {errors.confirmationPassword.message}
                      </FormHelperText>
                    )}
                  </FormControl>
                </Grid>
              </Grid>
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                onClick={handleSubmit(sendRegisterRequest)}
              >
                Sign Up
              </Button>
              <Grid container justifyContent="center">
                <Grid item>
                  <Link
                    variant="body2"
                    onClick={() => navigate("/login")}
                    className={"cursor-pointer"}
                  >
                    Already have an account? Sign in
                  </Link>
                </Grid>
              </Grid>
            </Box>
          </Box>
        </Container>
      </Grid>
    </Grid>
  );
};
