

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        // If sidebar is currently visible, hide it and adjust content margin
        $(".sidebar").hide();
        $(".content").css("margin-left", "0%");
    } else {
        // If sidebar is currently hidden, show it and adjust content margin
        $(".sidebar").show();
        $(".content").css("margin-left", "20%");
    }
};



const search = () => {
    //console.log("searching");
    let query = $("#search-input").val();
    
    if(query == ""){
		$(".search-result").hide();
		
	}else{
		//search
		console.log(query);
		
		//sending request to server
		let url = "http://localhost:7070/search/${query}";
		fetch(url)
		.then((response) => {
			return response.json();
		})
		.then((data)=>{
			console.log(data);
			let text=`<div class='list-group'>`;
			data.forEach((contact) => {
				
				text += `<a href='#'class='list-group-item list-group-action'>${contact.name}></a>`
			});
			text += `</div>`;
		});
		$(".search-result").show();
		}
};


