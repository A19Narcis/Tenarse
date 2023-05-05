const { query } = require('express');
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

const checkUserExists = async (username, callback) => {
    const userSelected = await User.findOne({ username: username });
    if (userSelected) {
        callback(true)
    } else {
        callback(false)
    }
}

const checkEmailExists = async (email, callback) => {
    const userSelected = await User.findOne({ email: email });
    if (userSelected) {
        callback(true)
    } else {
        callback(false)
    }
}

const getUserByID = async (id, callback) => {
    const userID = await User.findOne({ _id: id })
    callback(userID);
}

const suggestedUserInfoByID = async (id, callback) => {
    const userInfo = await User.findOne({ _id: id }, { _id: 1, username: 1, url_img: 1 })
    callback(userInfo)
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

const getPostsByHashtag = async (hashtag, callback) => {
    const posts = await Post.find({ hashtags: hashtag, $or: [{ tipus: 'image' }, { tipus: 'video' }] });
    callback(posts)
}

const getPostsByQuery = async (query, callback) => {
    const doubts = await Post.find({
        $or: [
          { titol: { $regex: query, $options: 'i' } },
          { text: { $regex: query, $options: 'i' } }
        ]
      });
    callback(doubts)
}



module.exports = {
    getUser,
    checkUserExists,
    checkEmailExists,
    getUserByID,
    getUsers,
    getChat,
    getPublicacio,
    getPosts,
    getPostsByHashtag,
    getPostsByQuery,
    suggestedUserInfoByID
}