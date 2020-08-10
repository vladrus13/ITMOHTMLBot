var programList = [];
var programCount = 0;

// Обработка события нажатия на звезду для добавления программы в корзину
$(document).on('click', '.favorite-star', function (e) {
    // На текущий момент нажатие происходит на картинку со зездой. См. module/programs.php
    var program = getProgram(e.target);
    var action = 'add';
    // Если программа уже добавлена, то требуется обратное действие (удаление)
    if (program.attr('data-favorite') === 'true') {
        action = 'remove';
    }
    // Обращаемся к API для добавление/удаление программы. Подробнее см. ajax/routes.php
    $.ajax({
        "url" : getBasketURL(action, program.data('id')),
        "data" : {}
    }).done(function(answ) {
        program.attr('data-favorite', program.attr('data-favorite') !== 'true');
        $(e.target).parent().toggleClass('in-basket');
        if ($(e.target).parent().hasClass('add-programm-wrapper')) {
            if (program.attr('data-favorite') === 'true') {
                $(e.target).html('Добавлено в избранное');
            }
            else {
                $(e.target).html('Добавить в избранное');
            }
        }
        updateFavorite(program, action);
        // @todo - возможно, именно здесь стоит вызвать обновление счетчика добавленных программ
    });
});

$(document).on('click', '.remove-favorite', function (e) {
    var number = parseInt($('.favorite-counter-number').text());
    var $self = $(this);
    var id = $self.data('id');
    $.ajax({
        "url" : getBasketURL('remove', id),
        "data" : {}
    }).done(function(answ) {
        $('.favorite-counter-number').text(--number);
        $('.favorite-program-'+id).remove();
    });
});

function updateFavorite(program, action) {
    var number = parseInt($('.favorite-counter-number').text());
    var id = program.data('id');
    var title = program.data('title');

    if (action == 'add') {
        $('.favorite-counter-number').text(++number);

        var listItem = $('<li>', {
            'class' : 'favorite-program-' + id
        });
        var programLink = $('<a>', {
            'href' : '/program/'+id+'/',
            'text' : title
        });
        var removeProgram = $('<a>', {
            'href'   : 'javascript:void(0)',
            'class'  : 'remove-favorite abiticons-close',
            'data-id': id
        });
        listItem.append(programLink).append(removeProgram);
        $('.favorite-modal-list').append(listItem);
    } else {
        $('.favorite-counter-number').text(--number)
        $('.favorite-program-'+id).remove();
    }
}


function getProgram(target) {
    return $(target).parent().parent().parent();
}

function getBasketURL(action, id) {
    var objectId = '';
    if (id) {
        objectId = id + '/';
    }
    return '/ajax/basket/' + action + '/' + objectId;
}

function loadFavoriteProgramInfo() {
    $.ajax({
        "url": getBasketURL('list')
    }).done(function (data) {
        programList = data;
        programCount = data.length;
    })
}

