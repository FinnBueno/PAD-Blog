function prepareEditElements(postId) {
    var targetPost = document.getElementById('post-' + postId);
    targetPost.getElementsByClassName('idInput')[0].value = postId;
    targetPost.getElementsByClassName('titleInput')[0].value = targetPost.getElementsByClassName('postTitle')[0].innerHTML;
    var inner = targetPost.getElementsByClassName('postContent')[0].innerHTML;
    targetPost.getElementsByClassName('contentInput')[0].value = inner.substring(17, inner.length - 13);
}

function preparePostEditElements(postId) {
    var targetPost = document.getElementById('post-' + postId);
    targetPost.getElementsByClassName('idInput')[0].value = postId;
    targetPost.getElementsByClassName('newTitle')[0].value = targetPost.getElementsByClassName('postTitle')[0].innerHTML;
    var inner = targetPost.getElementsByClassName('postContent')[0].innerHTML;
    targetPost.getElementsByClassName('newContent')[0].value = inner.substring(17, inner.length - 13);
}

function prepareNewPostElements() {
    var targetPost = document.getElementsByClassName('post');
    targetPost.getElementsByClassName('contentInput')[0].value = targetPost.getElementsByClassName('postContent')[0].innerHTML;
}