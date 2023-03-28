const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')


const getUser = async (nomUsuari, callback) => {
    const userSelected = await User.findOne({ username: nomUsuari})
    callback(userSelected);
}

const getChat = async (chat_id, callback) => {
    const chatSelected = await Chat.findOne({ _id: chat_id })
    callback(chatSelected);
}

const getPublicacio = async (id_publi, callback) => {
    const postSelected = await Post.findOne({ _id: id_publi })
    callback(postSelected)
}



module.exports = {
    getUser,
    getChat,
    getPublicacio
}