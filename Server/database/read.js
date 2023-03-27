const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

//Select 1 ususari específic
const getUser = async (nomUsuari, callback) => {
    const userSelected = await User.findOne({ username: nomUsuari})
    callback(userSelected);
}




module.exports = {
    getUser
}