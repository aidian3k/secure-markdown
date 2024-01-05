import React, { useState } from "react";
import Dropzone from "react-dropzone";
import MDEditor from "@uiw/react-md-editor";
import rehypeSanitize from "rehype-sanitize";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
  TextField,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import AppSnackbar from "../components/AppSnackbar";
import { LoadingButton } from "@mui/lab";
import { axiosApi } from "../tools/configuration/axios-config";
import axios, { AxiosError } from "axios";
import { NoteVisibility } from "../core/constants/NoteVisibility";
import { useNavigate } from "react-router";

const noteAddValidator = yup
  .object()
  .shape({
    title: yup.string().required("This field is required"),
    content: yup.string().required("Content is required to proceed"),
    encrypted: yup.boolean(),
    public: yup.boolean(),
    password: yup.string().when("encrypted", {
      is: true,
      then: () => yup.string().required("Password is required when encrypted"),
      otherwise: () => yup.string(),
    }),
  })
  .test({
    name: "exclusiveCheckboxes",
    test: function (values) {
      const { encrypted, public: isPublic } = values;
      return !(encrypted && isPublic);
    },

    message: 'Only one of "Encrypted" and "Public" can be checked at a time',
  });

type NoteCreationRequest = {
  title: string;
  content: string;
  encrypted?: boolean | undefined;
  public?: boolean | undefined;
  password?: string | undefined;
};

export const NoteAddPage: React.FC = () => {
  const {
    setValue,
    control,
    handleSubmit,
    formState: { errors },
    watch,
    reset,
  } = useForm({
    resolver: yupResolver(noteAddValidator),
  });
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleDrop = (acceptedFiles: File[]) => {
    const imageUrl = URL.createObjectURL(acceptedFiles[0]);

    setValue("content", `${watch("content")}\n![Uploaded Image](${imageUrl})`);
  };

  function getNoteVisibility(
    noteCreationRequest: NoteCreationRequest
  ): NoteVisibility {
    if (noteCreationRequest.public) {
      return NoteVisibility.PUBLIC;
    } else if (noteCreationRequest.encrypted) {
      return NoteVisibility.ENCRYPTED;
    } else {
      return NoteVisibility.PRIVATE;
    }
  }

  function sendNoteCreationRequest(noteCreationRequest: NoteCreationRequest) {
    setLoading(true);
    const noteVisibility: NoteVisibility =
      getNoteVisibility(noteCreationRequest);

    axiosApi.get("/api/auth/csrf").then((tokenResponse) => {
      const config = {
        headers: {
          "X-XSRF-TOKEN": tokenResponse.data.token,
        },
      };

      axiosApi
        .post(
          "/api/note/save-note",
          {
            content: noteCreationRequest.content,
            title: noteCreationRequest.title,
            noteVisibility: noteVisibility.toString(),
            notePassword: noteCreationRequest.password,
          },
          config
        )
        .then((result) => {
          if (result.status === 201) {
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
    });
  }

  return (
    <div className={"p-1"}>
      <AppSnackbar
        open={!!error}
        message={error}
        onClose={() => setError(null)}
      />
      <AppSnackbar
        open={success}
        message={"Successfully added note"}
        severity={"success"}
        duration={3000}
        onClose={() => {
          setSuccess(false);
          setError(null);
          reset();
          navigate("/");
        }}
      />
      <div className={"text-center flex gap-2 justify-center items-center"}>
        <p className={"text-3xl text-white font-bold"}>Adding new note</p>
        <AddCircleIcon fontSize="large" sx={{ color: "white" }} />
      </div>
      <TextField
        onChange={(event) => setValue("title", event.target.value)}
        inputProps={{ style: { color: "white" } }}
        error={!!errors.title}
        helperText={!!errors.title ? errors.title.message : ""}
        margin="normal"
        color="secondary"
        required
        fullWidth
        label="Title"
        autoFocus
      />
      <div className={"flex flex-col"}>
        <MDEditor
          value={watch("content")}
          height={375}
          onChange={(event) => {
            if (event === undefined) {
              return;
            }
            setValue("content", event);
          }}
          previewOptions={{
            rehypePlugins: [[rehypeSanitize]],
          }}
        />

        <div className={"cursor-pointer"}>
          <Dropzone onDrop={handleDrop} multiple={false}>
            {({ getRootProps, getInputProps }) => (
              <div
                {...getRootProps()}
                style={{ padding: "20px", border: "2px dashed white" }}
                className={""}
              >
                <input {...getInputProps()} />
                <p className="text-center text-white">
                  Drag and drop an image here, or click to select one from your
                  computer
                </p>
              </div>
            )}
          </Dropzone>
        </div>
      </div>
      <FormControl component="fieldset">
        <div className="flex gap-2 text-white">
          <Controller
            name="public"
            control={control}
            defaultValue={false}
            render={({ field }) => (
              <FormControlLabel
                control={<Checkbox {...field} />}
                label="Public"
              />
            )}
          />
          <Controller
            name="encrypted"
            control={control}
            defaultValue={false}
            render={({ field }) => (
              <FormControlLabel
                control={<Checkbox {...field} />}
                label="Encrypted"
              />
            )}
          />
        </div>
      </FormControl>

      {!!watch("encrypted") && (
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
            inputProps={{ style: { color: "white" } }}
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
      )}

      {!!errors.content ? (
        <FormHelperText error>{errors.content.message}</FormHelperText>
      ) : (
        ""
      )}

      <div className={"mt-4"}>
        <LoadingButton
          fullWidth
          loading={loading}
          variant="contained"
          onClick={handleSubmit(sendNoteCreationRequest)}
        >
          {!loading ? "Save note" : "Saving..."}
        </LoadingButton>
      </div>
    </div>
  );
};
