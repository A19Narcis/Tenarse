const mongoose = require('mongoose');

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const deletePost = async function (idPost, username, callback) {
    await Post.deleteOne({ _id: idPost });
    await User.updateOne(
        { username: username },
        { $pull: { publicacions: { _id: idPost } } }
    ).then(result => {
        callback();
    })
}


module.exports = {
    deletePost
}
