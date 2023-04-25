const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')


const getUser = async (email_username_id, callback) => {
    const userSelected = await User.findOne({ 
        $or: [
          { username: email_username_id },
          { email: email_username_id },
          { _id: email_username_id }
        ]
      });
    callback(userSelected);
}

const getUsers = async (text, callback) => {
    const usersFound = await User.find({ username: { $regex: text, $options: 'i' } })
    callback(usersFound);
}

const getChat = async (chat_id, callback) => {
    const chatSelected = await Chat.findOne({ _id: chat_id })
    callback(chatSelected);
}

const getPublicacio = async (id_publi, callback) => {
    const postSelected = await Post.findOne({ _id: id_publi })
    callback(postSelected)
}

const getPosts = async (callback) => {
    const posts = await Post.find()
    callback(posts)
}



module.exports = {
    getUser,
    getUsers,
    getChat,
    getPublicacio,
    getPosts
}