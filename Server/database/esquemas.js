const mongoose = require("mongoose")
const autoIncrement = require("mongoose-auto-increment")

autoIncrement.initialize(mongoose.connection)

mongoose.set('strictQuery', false);

const userSchema = new mongoose.Schema({
    username: {
        type: String,
        require: true,
        unique: true
    }
});

const User = mongoose.model('User', userSchema);

module.exports = {
    User
}