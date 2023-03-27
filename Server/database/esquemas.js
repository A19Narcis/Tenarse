const mongoose = require("mongoose")
const autoIncrement = require("mongoose-auto-increment")

autoIncrement.initialize(mongoose.connection)

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
    id_post: {
        type: Number,
        unique: true,
        required: true,
        autoIncrement: true
    },
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
    url_img: {
        type: String,
    },
    url_video: {
        type: String,
    },
    likes: {
        type: Number,
        default: 0,
    },
    comentaris: {
        type: [comSchema],
    },
    owner: {
        type: String,
        required: true
    }
});



/* USUARIS */
const followSchema = new mongoose.Schema({
    url_img: {
        type: String,
        required: true
    },
    username: {
        type: String,
        required: true
    }
})

const userSchema = new mongoose.Schema({
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
    following: {
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
        type: userSchema,
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
    participants: [userSchema],
    message: [msgSchema]
})


/* CREACIÓ MODELS */

publiSchema.plugin(autoIncrement.plugin, {model: 'Partida', field: 'id_post'});

const User = mongoose.model('User', userSchema);
const Post = mongoose.model('Post', publiSchema);
const Chat = mongoose.model('Chat', chatSchema);

module.exports = {
    User,
    Post,
    Chat
}