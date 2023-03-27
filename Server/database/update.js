const mongoose = require('mongoose');

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const readDB = require('./read')


const addUserPost = function (post, callback) {
    var username = post.owner;
    readDB.getUser(username, function (dades_user) {
        if (dades_user) {
            User.updateOne({ username: dades_user.username }, { $push: { publicacions: post } })
            .then (result => {
                callback();
            })
        }
    })
}

module.exports = {
    addUserPost
}