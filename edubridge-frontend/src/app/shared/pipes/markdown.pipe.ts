import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'markdown',
  standalone: true
})
export class MarkdownPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): SafeHtml {
    if (!value) return '';

    // Convert markdown-like syntax to HTML
    let html = value;

    // Headers
    html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>');
    html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>');
    html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>');

    // Bold
    html = html.replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>');

    // Italic
    html = html.replace(/\*(.*?)\*/gim, '<em>$1</em>');

    // Code blocks
    html = html.replace(/```([\s\S]*?)```/gim, '<pre><code>$1</code></pre>');

    // Inline code
    html = html.replace(/`([^`]+)`/gim, '<code>$1</code>');

    // Lists
    html = html.replace(/^\* (.*$)/gim, '<li>$1</li>');
    html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');

    html = html.replace(/^\d+\. (.*$)/gim, '<li>$1</li>');
    html = html.replace(/(<li>.*<\/li>)/s, '<ol>$1</ol>');

    // Line breaks
    html = html.replace(/\n\n/g, '</p><p>');
    html = html.replace(/\n/g, '<br>');

    // Wrap in paragraph if not already wrapped
    if (!html.startsWith('<')) {
      html = '<p>' + html + '</p>';
    }

    return this.sanitizer.sanitize(1, html) || '';
  }
}
