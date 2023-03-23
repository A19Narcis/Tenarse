const express = require('express');
const cors = require("cors");
const database = require('./database/connection');
const readDB = require('./database/read')
const insertDB = require('./database/create')
const updateDB = require('./database/update')
const deleteDB = require('./database/delete')
const app = express();

const PORT = 3000

app.use(express.json());
app.use(cors({
    credentials: true,
    origin: function (origin, callback) {
        return callback(null, true)
    }
}));



app.listen(PORT, () => {
    console.log("Server Running [" + PORT + "]");
});