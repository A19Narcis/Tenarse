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
    const usersFound = await User.find({
    $or: [
        { username: { $regex: text, $options: 'i' } },
        { nombre: { $regex: text, $options: 'i' } },
        { apellidos: { $regex: text, $options: 'i' } }
    ]
    });    
    callback(usersFound);
}

const getChat = async (chat_id, callback) => {
    const chatSelected = await Chat.findOne({ _id: chat_id })
    callback(chatSelected);
}

const getAllMyChats = async (user_id, callback) => {
    const chatsSelected = await Chat.find({ participants: { $in: [user_id] } })
    callback(chatsSelected);
}

const getPublicacio = async (id_publi, callback) => {
    const postSelected = await Post.findOne({ _id: id_publi })
    callback(postSelected)
}

const getPosts = async (numPagina, callback) => {
    const posts = await Post.find().sort({ hora: -1 }).skip(numPagina * 5).limit(5) //SKIP -> numPagina * numPostQueCargan - LIMIT .> numeroPostQueCargan
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

const getFollowersInfo = async (id_user, callback) => {
    var followersInfo = [];
    const user = await User.findOne({ _id: id_user }, { followers: 1 });    
    const followersPromise = user.followers.map(follower => {
      return new Promise(resolve => {
        getUserByID(follower.user, (dades_user) => {
          var infoUser = {};
          infoUser.username = dades_user.username;
          infoUser.url_img = dades_user.url_img;
          followersInfo.push(infoUser);
          resolve();
        });
      });
    });
    
    Promise.all(followersPromise).then(() => {
        followersInfo.sort((a, b) => a.username.localeCompare(b.username));
        callback(followersInfo);
    });
}

const getFollowingsInfo = async (id_user, callback) => {
    var followingsInfo = [];
    const user = await User.findOne({ _id: id_user }, { followings: 1 });    
    const followingsPromise = user.followings.map(following => {
      return new Promise(resolve => {
        getUserByID(following.user, (dades_user) => {
          var infoUser = {};
          infoUser.username = dades_user.username;
          infoUser.url_img = dades_user.url_img;
          followingsInfo.push(infoUser);
          resolve();
        });
      });
    });
    
    Promise.all(followingsPromise).then(() => {
        followingsInfo.sort((a, b) => a.username.localeCompare(b.username));
        callback(followingsInfo);
    });
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
    suggestedUserInfoByID,
    getAllMyChats,
    getFollowersInfo,
    getFollowingsInfo
}