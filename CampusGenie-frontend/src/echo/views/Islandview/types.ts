export interface ResourceDraft {
  resourceType: string;
  title: string;
  description: string;
  url: string;
  tags: string;
  reason: string;
}

export interface RulesEditPayload {
  proposedContentHtml: string;
  proposedText: string;
}
