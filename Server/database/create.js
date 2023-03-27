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
            callback("Aquest usuari ja es troba registrat")
        } else {
            callback("Usuari afegit a la base de dades")
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

module.exports = {
    insertUsuari,
    insertPost
}