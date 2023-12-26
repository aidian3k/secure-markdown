import { FC } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { ModalProperties } from "../../core/types/Modal.types";

export const RegisterSuccessModal: FC<ModalProperties & { qrURI: string }> = (
  props: ModalProperties & { qrURI: string }
) => {
  const { isOpen, handleClose } = props;

  return (
    <Dialog open={isOpen} onClose={handleClose}>
      <DialogTitle>Successful registration</DialogTitle>
      <DialogContent>
        <DialogContentText>
          <div
            className={"bg-green-600 text-white p-2 rounded-full text-center"}
          >
            Thanks for your registration. Please scan the QR code for generating
            MFA token for login.
          </div>
          <img src={props.qrURI} className={"px-4 w-full"} alt={"qr-code"} />
        </DialogContentText>
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center" }}>
        <Button onClick={handleClose} color={"success"} variant={"contained"}>
          Login
        </Button>
      </DialogActions>
    </Dialog>
  );
};
