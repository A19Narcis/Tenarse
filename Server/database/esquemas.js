const mongoose = require("mongoose")

mongoose.set('strictQuery', false);


/* PUBLICACIONS */
const comSchema = new mongoose.Schema({
    user_img: {
        type: String,
        required: true
    },
    user: {
        type: String,
        required: true
    },
    coment_text: {
        type: String,
        required: true,
        maxlength: 100
    }
})

const publiSchema = new mongoose.Schema({
    tipus: {
        type: String,
        required: true
    },
    titol: {
        type: String,
        maxlength: 75
    },
    text: {
        type: String,
        maxlength: 175
    },
    hashtags: {
        type: [String],
        required: true
    },
    url_img: {
        type: String,
    },
    url_video: {
        type: String,
    },
    likes: {
        type: [String],
        default: [],
    },
    comentaris: {
        type: [comSchema],
    },
    owner: {
        type: String,
        required: true
    },
    user_img: {
        type: String,
        required: true
    },
    hora: {
        type: String,
        required: true
    }
});



/* USUARIS */
const followSchema = new mongoose.Schema({
    user: {
        type: String,
        required: true
    }
})

const userSchema = new mongoose.Schema({
    email: {
        type: String,
        required: true,
        maxlength: 60,
        unique: true
    },
    username: {
        type: String,
        required: true,
        maxlength: 25,
        unique: true
    },
    password: {
        type: String,
        required: true,
    },
    url_img: {
        type: String,
        required: true,
    },
    nombre: {
        type: String,
        required: true,
    },
    apellidos: {
        type: String,
        required: true,
    },
    fecha_nac: {
        type: String,
        required: true,
    },
    followers: {
        type: [followSchema],
        default: []
    },
    followings: {
        type: [followSchema],
        default: []
    },
    publicacions: {
        type: [publiSchema],
        default: []
    }

});



/* CHAT */
const msgSchema = new mongoose.Schema({
    emisor: {
        type: String,
        required: true
    },
    txt_msg: {
        type: String,
        required: true,
    },
    hora: {
        type: String,
        required: true
    }
})

const chatSchema = new mongoose.Schema({
    participants: [String],
    messages: {
        type: [msgSchema],
        default: []
    }
})


/* CREACIÓ MODELS */

const User = mongoose.model('User', userSchema);
const Post = mongoose.model('Post', publiSchema);
const Chat = mongoose.model('Chat', chatSchema);

module.exports = {
    User,
    Post,
    Chat
}