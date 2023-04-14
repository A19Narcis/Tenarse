const mongoose = require('mongoose');

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const readDB = require('./read')
const updateDB = require('./update')

const insertUsuari = function (user, callback) {
    const newUser = new User(user);
    newUser.save(function (err) {
        if (err) {
            console.log(err);
            callback(false)
        } else {
            callback(true)
        }
    })
}

const insertPost = function (post, callback) {
    const newPost = new Post(post);
    newPost.save(function (err) {
        if (err) return console.log(err);
        updateDB.addUserPost(newPost, callback => {})
    });
    callback();
}


const insertChat = function (chat, callback) {
    const newChat = new Chat(chat);
    newChat.save(function (err) {
        if (err) return console.log(err);
    })
    callback();
}


module.exports = {
    insertUsuari,
    insertPost,
    insertChat
}