const mongoose = require('mongoose');

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const readDB = require('./read')


const addUserPost = function (post, callback) {
    var id_user = post.owner;
    readDB.getUserByID(id_user, function (dades_user) {
        if (dades_user) {
            User.updateOne({ _id: dades_user._id }, { $push: { publicacions: post } })
                .then(result => {
                    callback();
                })
        }
    })
}

const addMessageChat = function (message, chat_id, callback) {
    readDB.getChat(chat_id, function (dades_chat) {
        if (dades_chat) {
            Chat.updateOne({ _id: dades_chat._id }, { $push: { messages: message } })
                .then(result => {
                    callback();
                })
        }
    })
}


const addCommentPost = function (id_publi, comentari, callback) {
    readDB.getPublicacio(id_publi, function (dades_publi) {
        if (dades_publi) {
            Promise.all([
                Post.updateOne({ _id: id_publi }, { $push: { comentaris: comentari } }),
                User.updateOne({ 'publicacions._id': id_publi }, { $push: { 'publicacions.$.comentaris': comentari } })
            ]).then(() => {
                callback();
            }).catch((err) => {
                console.log(err);
            })
        }
    })
}

const addLikePost = function(id_publi, id_user, callback) {
    readDB.getPublicacio(id_publi, function (dades_publi) {
        if (dades_publi) {
            Promise.all([
                Post.updateOne({ _id: id_publi }, { $push: { likes: id_user } }),
                User.updateOne({ 'publicacions._id': id_publi }, { $push: { 'publicacions.$.likes': id_user } })
            ]).then(() => {
                callback();
            }).catch((err) => {
                console.log(err);
            })
        }
    })
}


const removeLikePost = function(id_publi, id_user, callback) {
    readDB.getPublicacio(id_publi, function (dades_publi) {
        if (dades_publi) {
            Promise.all([
                Post.updateOne({ _id: id_publi }, { $pull: { likes: id_user } }),
                User.updateOne({ 'publicacions._id': id_publi }, { $pull: { 'publicacions.$.likes': id_user } })
            ]).then(() => {
                callback();
            }).catch((err) => {
                console.log(err);
            })
        }
    })
}

/* Afegir un following i, per tant, també un follower a la persona que has seguit */
const addFollowingUser = function (following, user_followed, user, callback) {
    readDB.getUserByID(user, function (dades_user) {
        if (dades_user) {
            //Afegir following
            User.updateOne({ _id: dades_user._id }, { $push: { followings: following } })
                .then(() => {
                    //Agefir follower
                    var follower = {
                        user: dades_user._id
                    }
                    User.updateOne({ _id: user_followed._id }, { $push: { followers: follower } })
                        .then(() => {
                            callback();
                        })
                })

        }
    })
}


/* Treure un following i, per tant, també un follower a la persona que has deixat de seguir */
const remFollowingUser = function (user_following, user_removed, callback) {
    User.updateOne({ _id: user_following }, { $pull: { followings: { user: user_removed } } })
        .then(() => {
            return User.updateOne({ _id: user_removed }, { $pull: { followers: { user: user_following } } })
        }).then(() => {
            callback();
        })
}

const updateUser = function (id_user, newDadesUser, callback) {
    User.updateOne(
        { _id: id_user }, 
        { 
            email: newDadesUser.email,
            username: newDadesUser.username,
            nombre: newDadesUser.nombre,
            apellidos: newDadesUser.apellidos,
            url_img: newDadesUser.url_img,
            fecha_nac: newDadesUser.fecha_nac
        }
    ).then(() => {
        readDB.getUserByID(id_user, (newDadesUser) => {
            callback(newDadesUser)
        })
    })
}



module.exports = {
    addUserPost,
    addMessageChat,
    addCommentPost,
    addLikePost,
    removeLikePost,
    addFollowingUser,
    remFollowingUser,
    updateUser
}