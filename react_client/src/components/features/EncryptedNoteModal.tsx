import * as yup from "yup";
import { FC, useState } from "react";
import { ModalProperties } from "../../core/types/Modal.types";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { axiosApi } from "../../tools/configuration/axios-config";
import { NoteDTO } from "./main-page/MainPageNote.component";
import { LoadingButton } from "@mui/lab";
import { useNavigate } from "react-router";
import axios, { AxiosError } from "axios";
import AppSnackbar from "../AppSnackbar";

const seeEncryptedNoteValidator = yup.object().shape({
  password: yup
    .string()
    .required("Password field is required when wanting to see encrypted note"),
});

type NoteViewDTO = {
  password: string;
};

export const EncryptedNoteModal: FC<
  ModalProperties & { noteId: string; setNote: any }
> = (
  props: ModalProperties & {
    noteId: string;
    setNote: any;
  }
) => {
  const {
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(seeEncryptedNoteValidator),
  });

  const { isOpen, handleClose } = props;
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  function sendPasswordResetRequest(resetPasswordRequest: NoteViewDTO) {
    setLoading(true);
    axiosApi
      .post<NoteDTO>(`/api/note/view/${props.noteId}`, resetPasswordRequest)
      .then((result) => {
        props.setNote(result.data);
        handleClose();
      })
      .catch((error) => {
        if (axios.isAxiosError(error)) {
          const axiosError: AxiosError = error as AxiosError;
          if (axiosError.response && axiosError.response.status === 400) {
            setError(JSON.stringify(axiosError.response.data));
          } else {
            setError("Error occured when trying to see encrypted note");
          }
        } else {
          setError("Error occured when trying to see encrypted note");
        }
      })
      .finally(() => setLoading(false));
  }

  return (
    <Dialog
      open={isOpen}
      onClose={(event: any, reason: any) => {
        if (reason && reason === "backdropClick") {
          return;
        }
        handleClose();
      }}
    >
      <AppSnackbar
        open={!!error}
        message={error}
        onClose={() => setError(null)}
      />
      <DialogTitle>Encrypted note details</DialogTitle>
      <DialogContent>
        <DialogContentText>
          To see the details of this encrypted note you have to pass the
          password which you entered during the creation
        </DialogContentText>
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
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center" }}>
        <LoadingButton
          onClick={handleSubmit(sendPasswordResetRequest)}
          loading={loading}
          color={"success"}
          variant={"contained"}
        >
          See note
        </LoadingButton>
        <LoadingButton loading={loading} onClick={() => {
          navigate('/note?visibility=encrypted')
          handleClose();
        }} color={"error"} variant={"contained"}>
          Cancel
        </LoadingButton>
      </DialogActions>
    </Dialog>
  );
};
