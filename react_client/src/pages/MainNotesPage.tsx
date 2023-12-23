import { FC, useState } from "react";
import { MainPageNote } from "../components/features/main-page/MainPageNote.component";
import { Container, Grid, Paper, TextField, Typography } from "@mui/material";

export const MainNotesPage: FC = () => {
  const sectionNames = ["Public", "Private", "Encrypted"];
  const [section1Notes, setSection1Notes] = useState([<MainPageNote />]);
  const [section2Notes, setSection2Notes] = useState([]);
  const [section3Notes, setSection3Notes] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  return (
    <Container maxWidth="xl">
      <Typography variant="h3" gutterBottom>
        Main Page
      </Typography>

      <TextField
        label="Search"
        variant="outlined"
        fullWidth
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{ marginBottom: 20 }}
      />

      <Grid container spacing={3}>
        {[section1Notes, section2Notes, section3Notes].map(
          (sectionNotes, index) => (
            <Grid item xs={12} key={index}>
              <Paper elevation={3} style={{ padding: 10 }}>
                <Typography variant="h6" gutterBottom>
                  {sectionNames[index]}
                </Typography>
                <ul>
                  {sectionNotes.map((note, noteIndex) => (
                    <li key={noteIndex}>{note}</li>
                  ))}
                </ul>
              </Paper>
            </Grid>
          )
        )}
      </Grid>
    </Container>
  );
};
