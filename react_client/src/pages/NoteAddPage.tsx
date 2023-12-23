import React, { useState } from "react";
import Dropzone from "react-dropzone";
import MDEditor from "@uiw/react-md-editor";
import rehypeSanitize from "rehype-sanitize";
import {
  Button,
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
  } = useForm({
    resolver: yupResolver(noteAddValidator),
  });
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const handleDrop = (acceptedFiles: File[]) => {
    const imageUrl = URL.createObjectURL(acceptedFiles[0]);

    setValue("content", `${watch("content")}\n![Uploaded Image](${imageUrl})`);
  };

  function sendNoteCreationRequest(noteCreationRequest: NoteCreationRequest) {
    console.log(noteCreationRequest);
  }

  return (
    <div className={"p-1"}>
      <div className={"text-center flex gap-2 justify-center items-center"}>
        <p className={"text-3xl text-white font-bold"}>Adding new note</p>
        <AddCircleIcon fontSize="large" sx={{ color: "white" }} />
      </div>
      <TextField
        onChange={(event) => setValue("title", event.target.value)}
        sx={{ color: "white" }}
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
        <Button
          fullWidth
          variant="contained"
          onClick={handleSubmit(sendNoteCreationRequest)}
        >
          Save note
        </Button>
      </div>
    </div>
  );
};
