const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')


const getUser = async (email_username, callback) => {
    const userSelected = await User.findOne({ 
        $or: [
          { username: email_username },
          { email: email_username }
        ]
      });
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