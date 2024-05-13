import React from "react";
import { Link } from "react-router-dom";
const SearchResult = ({ url, title, description, searchquery }) => {
	const highlightWords = (description, searchquery) => {
		const words = description.split(/\s+/);
		const wordsToHighlight = searchquery.toLowerCase().split(/\s+/);

		let result = [];
		let wordIndex = 0;

		words.forEach((word, index) => {
			const isHighlighted = wordsToHighlight.includes(word.toLowerCase());
			result.push(
				<span key={index} className={isHighlighted ? " text-xl font-black" : ""}>
					{word}
				</span>
			);

			// Add space after each word except the last one
			if (wordIndex < words.length - 1) {
				result.push(" ");
			}
			wordIndex++;
		});

		return result;
	};

	return (
		<div className="border-2 border-[#9E9E9E]  rounded-md p-4 my-4 w-full bg-[rgba(0,0,0,0.09)]">
			<h2 className="text-xl text-start font-serif font-normal text-nowrap  text-[#232222]">
				{url}
			</h2>
			<Link
				to={url}
				className="text-[2.25rem] text-start font-serif font-bold text-nowrap my-1 hover:underline hover:text-[#c35353] text-[#232222]">
				{title}
			</Link>
			<h3 className="text-lg text-start font-serif font-extralight text-wrap text-[#232222] my-[5px]">
				{/* {description.substring(0, Math.min(100, description.length)) + "..."} */}
				<p>{highlightWords(description, searchquery)}</p>
			</h3>
		</div>
	);
};

export default SearchResult;
