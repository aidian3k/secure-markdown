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

const seeEncryptedNoteValidator = yup.object().shape({
  password: yup
    .string()
    .required("Password field is required when wanting to see encrypted note"),
});

type NoteViewDTO = {
  password: string;
};

export const EncryptedNoteModal: FC<ModalProperties> = (
  props: ModalProperties
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

  function sendPasswordResetRequest(resetPasswordRequest: NoteViewDTO) {
    console.log(resetPasswordRequest);
  }

  return (
    <Dialog open={isOpen} onClose={handleClose}>
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
        <Button
          onClick={handleSubmit(sendPasswordResetRequest)}
          color={"success"}
          variant={"contained"}
        >
          See note
        </Button>
        <Button onClick={handleClose} color={"error"} variant={"contained"}>
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
};
